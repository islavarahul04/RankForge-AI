from rest_framework import status, views, viewsets
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, IsAdminUser, AllowAny
from rest_framework_simplejwt.tokens import RefreshToken
from rest_framework import permissions
from django.conf import settings
from django.contrib.auth import authenticate, get_user_model
from django.core.cache import cache
from django.db.models import Count, Avg
from datetime import date, timedelta
import groq
import os
import random

from .models import (User, Notification, GlobalNotification, ExamCategory, StudySubject, StudyTopic, 
                    UserTopicProgress, UserMockTestResult, MockTest, Question, SupportTicket, PYQPaper, SubscriptionPlan, UserUnlockedTest, DailyCheckIn, AiChatMessage)
from .serializers import (UserSerializer, RegistrationSerializer, NotificationSerializer, ExamCategorySerializer, StudySubjectSerializer, 
                        StudyTopicSerializer, QuestionSerializer, MockTestSerializer, MockTestListSerializer, 
                        UserMockTestResultSerializer, SupportTicketSerializer, PYQPaperSerializer, AiChatMessageSerializer, SubscriptionPlanSerializer)

User = get_user_model()

class IsPlatformAdmin(permissions.BasePermission):
    def has_permission(self, request, view):
        return bool(request.user and request.user.is_authenticated and 
                    (request.user.is_staff or request.user.is_superuser or request.user.is_admin))

class RegisterView(views.APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        serializer = RegistrationSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            refresh = RefreshToken.for_user(user)
            return Response({
                'user': UserSerializer(user, context={'request': request}).data,
                'refresh': str(refresh),
                'access': str(refresh.access_token),
            }, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class LoginView(views.APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        try:
            email = request.data.get('email')
            password = request.data.get('password')
            
            user = authenticate(email=email, password=password)
            
            if user:
                refresh = RefreshToken.for_user(user)
                return Response({
                    'user': UserSerializer(user, context={'request': request}).data,
                    'refresh': str(refresh),
                    'access': str(refresh.access_token),
                })
            return Response({'error': 'Invalid Credentials'}, status=status.HTTP_401_UNAUTHORIZED)
        except Exception as e:
            import traceback
            print(f"Login Error: {str(e)}")
            print(traceback.format_exc())
            return Response({'error': str(e), 'traceback': traceback.format_exc()}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

class AdminLoginView(views.APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        try:
            email = request.data.get('email')
            password = request.data.get('password')
            user = authenticate(email=email, password=password)
            if user:
                if user.is_staff or user.is_superuser or user.is_admin:
                    refresh = RefreshToken.for_user(user)
                    return Response({
                        'user': UserSerializer(user, context={'request': request}).data,
                        'refresh': str(refresh),
                        'access': str(refresh.access_token),
                    })
                else:
                    return Response({'error': 'Unauthorized! Admin credentials required.'}, status=status.HTTP_403_FORBIDDEN)
            return Response({'error': 'Invalid Credentials'}, status=status.HTTP_401_UNAUTHORIZED)
        except Exception as e:
            import traceback
            print(f"Admin Login Error: {str(e)}")
            print(traceback.format_exc())
            return Response({'error': str(e), 'traceback': traceback.format_exc()}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

class AdminDashboardStatsView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)

        today = date.today()
        yesterday = today - timedelta(days=1)

        def calculate_percentage(current, previous):
            if previous == 0:
                return "+100%" if current > 0 else "0%"
            change = ((current - previous) / previous) * 100
            sign = "+" if change >= 0 else ""
            return f"{sign}{int(change)}%"

        # 1. Total Users (excluding staff)
        total_users = User.objects.filter(is_staff=False, is_superuser=False).count()
        users_yesterday = User.objects.filter(is_staff=False, is_superuser=False, date_joined__lt=today).count()
        users_percentage = calculate_percentage(total_users, users_yesterday)

        # 2. Active Today (distinct check-ins today vs yesterday)
        active_today = DailyCheckIn.objects.filter(date=today).values('user').distinct().count()
        active_yesterday = DailyCheckIn.objects.filter(date=yesterday).values('user').distinct().count()
        active_percentage = calculate_percentage(active_today, active_yesterday)

        # 3. Revenue (Mock logic: count active premium users)
        premium_users = User.objects.filter(is_premium=True).count()
        revenue = premium_users * 999
        
        premium_users_yesterday = User.objects.filter(is_premium=True, date_joined__lt=today).count()
        revenue_yesterday = premium_users_yesterday * 999
        revenue_percentage = calculate_percentage(revenue, revenue_yesterday)

        # 4. Tests Taken
        tests_taken_today = UserMockTestResult.objects.filter(created_at__gte=today).count()
        tests_taken_yesterday = UserMockTestResult.objects.filter(created_at__gte=yesterday, created_at__lt=today).count()
        
        # Override tests_taken logic to just return the total count in the platform, 
        # but percentage reflects growth of activity today vs yesterday.
        total_tests = UserMockTestResult.objects.count()
        tests_percentage = calculate_percentage(tests_taken_today, tests_taken_yesterday)

        return Response({
            "total_users": total_users,
            "total_users_percentage": users_percentage,
            "active_today": active_today,
            "active_today_percentage": active_percentage,
            "revenue": revenue,
            "revenue_percentage": revenue_percentage,
            "premium_users": premium_users,
            "tests_taken": total_tests,
            "tests_taken_percentage": tests_percentage,
            "recent_activity": GlobalNotification.objects.order_by('-created_at')[:5].values('title', 'message', 'category', 'created_at')
        })

class AdminNotificationCreateView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)

        title = request.data.get('title', 'System Alert')
        message = request.data.get('message')
        category = request.data.get('category', 'General')

        if not message:
            return Response({'error': 'Message is required'}, status=status.HTTP_400_BAD_REQUEST)

        # 1. Save to Global history
        GlobalNotification.objects.create(
            title=title,
            message=message,
            category=category
        )

        # 2. Broadcast to ALL users
        users = User.objects.filter(is_staff=False, is_superuser=False)
        notifications = [
            Notification(user=user, title=title, message=message, category=category)
            for user in users
        ]
        Notification.objects.bulk_create(notifications)

        return Response({'message': f'Successfully broadcasted to {len(notifications)} users.'})

class AdminUserListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)

        # Get all standard platform users with calculated fields
        users_list = []
        users = User.objects.filter(is_staff=False, is_superuser=False)
        
        # Pre-calculate name counts for duplicate detection (matching app logic)
        from django.db.models import Count
        duplicate_names = list(users.values('full_name').annotate(name_count=Count('full_name')).filter(name_count__gt=1).values_list('full_name', flat=True))
        
        for u in users:
            users_list.append({
                'email': u.email,
                'full_name': u.full_name or "RankForge User",
                'is_active': u.is_active,
                'is_premium': u.is_premium,
                'premium_plan': u.premium_plan or "Free",
                'date_joined': u.date_joined.isoformat(),
                'tests_attempted': u.mock_test_results.count(),
                'is_duplicate': (u.full_name in duplicate_names) if u.full_name else False
            })
            
        return Response(users_list)

from django.core.mail import send_mail
from django.conf import settings

class ForgotPasswordView(views.APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get('email')
        try:
            user = User.objects.get(email=email)
            otp = str(random.randint(1000, 9999))
            cache.set(f"otp_{email}", otp, timeout=300) # 5 minutes
            
            # Send real email using Brevo SMTP
            subject = "RankForge AI - Your Verification Code"
            message = f"Hello,\n\nYour 4-digit verification code is: {otp}\n\nThis code is valid for 5 minutes. If you did not request this, please ignore this email.\n\nBest regards,\nRankForge AI Team"
            
            try:
                send_mail(
                    subject,
                    message,
                    settings.DEFAULT_FROM_EMAIL,
                    [email],
                    fail_silently=False,
                )
                return Response({'message': 'OTP sent successfully to your email'})
            except Exception as e:
                print(f"SMTP Error: {str(e)}")
                return Response({'error': 'Failed to send OTP email. Please try again later.'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
                
        except User.DoesNotExist:
            return Response({'error': 'User with this email not found'}, status=status.HTTP_404_NOT_FOUND)

class VerifyOTPView(views.APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get('email')
        otp = request.data.get('otp')
        cached_otp = cache.get(f"otp_{email}")

        if cached_otp and cached_otp == otp:
            user = User.objects.get(email=email)
            refresh = RefreshToken.for_user(user)
            # We don't delete the OTP yet, or we use a session/token to authorize reset
            # For simplicity, returning a token that ResetPasswordView will use
            return Response({
                'message': 'OTP verified',
                'refresh': str(refresh),
                'access': str(refresh.access_token),
            })
        return Response({'error': 'Invalid or expired OTP'}, status=status.HTTP_400_BAD_REQUEST)

class ResetPasswordView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        new_password = request.data.get("new_password")
        if not new_password:
            return Response({"error": "New password is required"}, status=status.HTTP_400_BAD_REQUEST)

        user = request.user
        user.set_password(new_password)
        user.save()
        
        # Invalidate OTP after successful reset
        cache.delete(f"otp_{user.email}")
        
        return Response({"message": "Password has been reset successfully."})

class ProfileView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        serializer = UserSerializer(request.user, context={'request': request})
        return Response(serializer.data)

    def put(self, request):
        serializer = UserSerializer(request.user, data=request.data, partial=True, context={'request': request})
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request):
        user = request.user
        user.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

class NotificationListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        notifications = Notification.objects.filter(user=request.user).order_by('-created_at')
        serializer = NotificationSerializer(notifications, many=True)
        return Response(serializer.data)

    def put(self, request):
        # Mark all as read
        Notification.objects.filter(user=request.user, is_read=False).update(is_read=True)
        return Response({"message": "All notifications marked as read."})

class StreakView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        today = date.today()
        created = False
        if not DailyCheckIn.objects.filter(user=request.user, date=today).exists():
            DailyCheckIn.objects.create(user=request.user, date=today)
            created = True
        return Response({"message": "Checked in successfully", "new_check_in": created})

    def get(self, request):
        checkins = DailyCheckIn.objects.filter(user=request.user).order_by('date')
        dates = [c.date for c in checkins]
        
        # Calculate current streak
        current_streak = 0
        today = date.today()
        
        # Check if user checked in today or yesterday to maintain active streak
        if dates and (dates[-1] == today or dates[-1] == today - timedelta(days=1)):
            current_date = dates[-1]
            current_streak = 1
            idx = len(dates) - 2
            while idx >= 0:
                if dates[idx] == current_date - timedelta(days=1):
                    current_streak += 1
                    current_date = dates[idx]
                    idx -= 1
                else:
                    break
        
        # Calculate longest streak
        longest_streak = 0
        if dates:
            temp_longest = 1
            for i in range(1, len(dates)):
                if dates[i] == dates[i-1] + timedelta(days=1):
                    temp_longest += 1
                else:
                    longest_streak = max(longest_streak, temp_longest)
                    temp_longest = 1
            longest_streak = max(longest_streak, temp_longest)
            
        return Response({
            "current_streak": current_streak,
            "longest_streak": longest_streak,
            "checked_in_dates": [d.strftime('%Y-%m-%d') for d in dates]
        })

class ExamCategoryListView(views.APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        categories = ExamCategory.objects.all().order_by('order')
        serializer = ExamCategorySerializer(categories, many=True)
        return Response(serializer.data)

class StudySubjectListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        subjects = StudySubject.objects.all().order_by('order')
        serializer = StudySubjectSerializer(subjects, many=True, context={'request': request})
        return Response(serializer.data)

class TopicProgressUpdateView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        topic_id = request.data.get('topic_id')
        is_completed = request.data.get('is_completed', False)

        if not topic_id:
            return Response({"error": "topic_id is required"}, status=400)

        try:
            topic = StudyTopic.objects.get(id=topic_id)
        except StudyTopic.DoesNotExist:
            return Response({"error": "Topic not found"}, status=404)

        progress, created = UserTopicProgress.objects.get_or_create(user=request.user, topic=topic)
        progress.is_completed = is_completed
        progress.save()

        return Response({"message": "Progress updated successfully", "is_completed": progress.is_completed})

class MockTestListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        tests = MockTest.objects.all().order_by('-id')
        from .serializers import MockTestListSerializer
        serializer = MockTestListSerializer(tests, many=True, context={'request': request})
        return Response(serializer.data)

class SubmitMockTestResultView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        test_id = request.data.get('test_id')
        score = request.data.get('score', 0)

        total_questions = request.data.get('total_questions', 100)
        correct_count = request.data.get('correct_count', 0)
        incorrect_count = request.data.get('incorrect_count', 0)
        eng_score = request.data.get('eng_score', 0)
        quant_score = request.data.get('quant_score', 0)
        reason_score = request.data.get('reason_score', 0)
        gk_score = request.data.get('gk_score', 0)
        selected_answers = request.data.get('selected_answers', [])

        if not test_id:
            return Response({"error": "test_id is required"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            test_obj = MockTest.objects.get(id=test_id)
        except MockTest.DoesNotExist:
            return Response({"error": "MockTest not found"}, status=status.HTTP_404_NOT_FOUND)

        # Always create a new attempt result
        result = UserMockTestResult.objects.create(
            user=request.user,
            test=test_obj,
            score=score,
            total_questions=total_questions,
            correct_count=correct_count,
            incorrect_count=incorrect_count,
            eng_score=eng_score,
            quant_score=quant_score,
            reason_score=reason_score,
            gk_score=gk_score,
            selected_answers=selected_answers,
            is_completed=True
        )

        from .serializers import UserMockTestResultSerializer
        serializer = UserMockTestResultSerializer(result)
        return Response(serializer.data)

class SubscriptionPlanListView(views.APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        plans = SubscriptionPlan.objects.filter(is_active=True).order_by('price')
        serializer = SubscriptionPlanSerializer(plans, many=True)
        return Response(serializer.data)

class AdminSubscriptionPlanViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAdminUser]
    queryset = SubscriptionPlan.objects.all().order_by('price')
    serializer_class = SubscriptionPlanSerializer

class SubscribeView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        plan_id = request.data.get('plan_id')
        if not plan_id:
            return Response({"error": "plan_id is required"}, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            plan = SubscriptionPlan.objects.get(id=plan_id, is_active=True)
        except SubscriptionPlan.DoesNotExist:
            return Response({"error": "Invalid or inactive subscription plan"}, status=status.HTTP_400_BAD_REQUEST)

        from django.utils import timezone
        from datetime import timedelta
        
        user = request.user
        user.is_premium = True
        user.premium_plan = plan.name
        user.premium_expiry = timezone.now() + timedelta(days=plan.duration_days)
        user.save()

        return Response({
            "message": f"Successfully subscribed to {plan.name} plan!",
            "user": UserSerializer(user).data
        })

class TestHistoryView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        results = UserMockTestResult.objects.filter(user=request.user).order_by('-created_at')
        serializer = UserMockTestResultSerializer(results, many=True)
        return Response(serializer.data)

class SupportTicketView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        tickets = SupportTicket.objects.filter(user=request.user).order_by('-created_at')
        serializer = SupportTicketSerializer(tickets, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = SupportTicketSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save(user=request.user)
            return Response({"message": "Support ticket created successfully", "ticket": serializer.data}, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class AdminSupportTicketView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        
        tickets = SupportTicket.objects.all().order_by('-created_at')
        
        # Include user info in admin view
        data = []
        for ticket in tickets:
            data.append({
                "id": ticket.id,
                "user_email": ticket.user.email,
                "user_name": ticket.user.full_name or "Anonymous",
                "message": ticket.message,
                "admin_reply": ticket.admin_reply,
                "replied_at": ticket.replied_at,
                "is_resolved": ticket.is_resolved,
                "created_at": ticket.created_at
            })
        return Response(data)

    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        
        ticket_id = request.data.get('ticket_id')
        reply = request.data.get('reply')
        is_resolved = request.data.get('is_resolved', True)

        if not ticket_id or not reply:
            return Response({"error": "ticket_id and reply are required"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            ticket = SupportTicket.objects.get(id=ticket_id)
            ticket.admin_reply = reply
            import django.utils.timezone as timezone
            ticket.replied_at = timezone.now()
            ticket.is_resolved = is_resolved
            ticket.save()
            return Response({"message": "Reply sent successfully"})
        except SupportTicket.DoesNotExist:
            return Response({"error": "Ticket not found"}, status=status.HTTP_404_NOT_FOUND)

class AdminExamCategoryViewSet(viewsets.ModelViewSet):
    permission_classes = [IsPlatformAdmin]
    queryset = ExamCategory.objects.all().order_by('order')
    serializer_class = ExamCategorySerializer

class AdminStudySubjectViewSet(viewsets.ModelViewSet):
    permission_classes = [IsPlatformAdmin]
    queryset = StudySubject.objects.all().order_by('order')
    serializer_class = StudySubjectSerializer

    def get_queryset(self):
        category_id = self.request.query_params.get('category_id')
        if category_id:
            return self.queryset.filter(category_id=category_id)
        return self.queryset

class AdminStudyTopicViewSet(viewsets.ModelViewSet):
    permission_classes = [IsPlatformAdmin]
    queryset = StudyTopic.objects.all().order_by('order')
    serializer_class = StudyTopicSerializer

    def get_queryset(self):
        subject_id = self.request.query_params.get('subject_id')
        if subject_id:
            return self.queryset.filter(subject_id=subject_id)
        return self.queryset

class ChangePasswordView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        user = request.user
        current_password = request.data.get("current_password")
        new_password = request.data.get("new_password")

        if not current_password or not new_password:
            return Response({"error": "Both current and new passwords are required."}, status=status.HTTP_400_BAD_REQUEST)

        if not user.check_password(current_password):
            return Response({"error": "Incorrect current password."}, status=status.HTTP_400_BAD_REQUEST)

        user.set_password(new_password)
        user.save()

        return Response({"message": "Password changed successfully."})

class AdminMockTestReportView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)

        # Get all mock tests that have at least one submission
        # Use aggregation to get counts and averages
        reports = MockTest.objects.annotate(
            attempts_count=Count('user_results'),
            average_score=Avg('user_results__score')
        ).filter(attempts_count__gt=0).order_by('-attempts_count')

        data = []
        for report in reports:
            data.append({
                "id": report.id,
                "name": report.name,
                "attempts_count": report.attempts_count,
                "average_score": round(report.average_score, 2) if report.average_score else 0
            })

        return Response(data)

class AdminTestResultsDetailView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, test_id):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)

        try:
            test = MockTest.objects.get(id=test_id)
        except MockTest.DoesNotExist:
            return Response({"error": "Mock test not found"}, status=404)

        results = UserMockTestResult.objects.filter(test=test).select_related('user').order_by('-score')
        
        data = []
        for res in results:
            data.append({
                "userName": res.user.full_name or "Anonymous User",
                "email": res.user.email,
                "score": res.score,
                "completed": res.is_completed,
                "date": res.created_at.strftime('%Y-%m-%d %H:%M')
            })

        return Response({
            "test_name": test.name,
            "results": data
        })

class AiChatView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        messages = AiChatMessage.objects.filter(user=request.user).order_by('created_at')
        serializer = AiChatMessageSerializer(messages, many=True)
        return Response(serializer.data)

    def post(self, request):
        user_message_text = request.data.get('message')
        if not user_message_text:
            return Response({"error": "Message is required"}, status=status.HTTP_400_BAD_REQUEST)

        print(f"DEBUG: Chat request from {request.user.email}")

        # Check for chat limits (Basic users: 20 chats/day)
        if not request.user.is_premium_active:
            from django.utils import timezone
            today = timezone.now().date()
            user_msg_count = AiChatMessage.objects.filter(
                user=request.user, 
                is_user=True, 
                created_at__date=today
            ).count()
            
            if user_msg_count >= 20:
                return Response({
                    "error": "limit_exhausted",
                    "message": "Daily limit reached. Try premium for unlimited chats."
                }, status=status.HTTP_403_FORBIDDEN)

        # Save user message
        AiChatMessage.objects.create(user=request.user, message=user_message_text, is_user=True)

        # Generate AI response based on user data
        ai_response_text = self._generate_ai_response(request.user, user_message_text)

        # Save AI response
        ai_message = AiChatMessage.objects.create(user=request.user, message=ai_response_text, is_user=False)
        
        return Response(AiChatMessageSerializer(ai_message).data, status=status.HTTP_201_CREATED)

    def _generate_ai_response(self, user, message_text):
        # Robust name retrieval
        name = user.full_name
        if not name or name.strip() == "":
            name = user.email.split('@')[0].capitalize()
        
        exam = user.target_exam or "competitive exams"
        
        # Get chat history for context (last 10 messages)
        history = AiChatMessage.objects.filter(user=user).order_by('-created_at')[:10]
        messages = [
            {
                "role": "system",
                "content": (
                    f"You are RankForge AI, an expert doubt-solving assistant EXCLUSIVELY for the SSC CHSL exam. "
                    f"The user's name is {name}. "
                    f"STRICT SCOPE: You only answer questions related to the SSC CHSL syllabus, which includes: "
                    f"1. Quantitative Aptitude, 2. General Intelligence (Reasoning), 3. English Language, and 4. General Awareness. "
                    f"REJECTION CRITERIA: If the user asks about ANY topic outside this syllabus (e.g., other exams like UPSC/JEE, "
                    f"coding, general knowledge not in syllabus, personal advice, etc.), you MUST politely decline. "
                    f"Example rejection: 'I'm sorry, I am specialized in SSC CHSL preparation and can only assist with topics from its syllabus.' "
                    f"Always maintain a supportive and educational tone within the allowed scope."
                )
            }
        ]
        
        # Add history in chronological order
        for msg in reversed(history):
            role = "user" if msg.is_user else "assistant"
            messages.append({"role": role, "content": msg.message})
            
        # Add current message
        messages.append({"role": "user", "content": message_text})

        try:
            client = groq.Groq(api_key=settings.GROQ_API_KEY)
            completion = client.chat.completions.create(
                model="llama-3.3-70b-versatile",
                messages=messages,
                temperature=0.7,
                max_tokens=1024,
                top_p=1,
                stream=False,
                stop=None,
            )
            return completion.choices[0].message.content
        except Exception as e:
            print(f"Groq API Error: {str(e)}")
            # Fallback for first message/greeting
            ai_msg_count = AiChatMessage.objects.filter(user=user, is_user=False).count()
            if ai_msg_count == 0 or any(x in message_text.lower() for x in ["hello", "hi"]):
                return f"Hello, {name}! I'm RankForge AI. I'm having a bit of trouble connecting to my brain right now, but I'm here to help you with {exam}. What's on your mind?"
            return f"I apologize, {name}, but I'm experiencing a temporary technical issue. Please try again in a moment."
class MockTestDetailView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, test_id):
        try:
            test = MockTest.objects.prefetch_related('questions').get(id=test_id)
            serializer = MockTestSerializer(test, context={'request': request})
            return Response(serializer.data)
        except MockTest.DoesNotExist:
            return Response({"error": "Test not found"}, status=status.HTTP_404_NOT_FOUND)

class AdminCreateTestView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        
        test_name = request.data.get('name')
        is_free = request.data.get('is_free', False)
        questions_data = request.data.get('questions', [])
        print("DEBUG AdminCreateTestView payload:", request.data)
        
        if not test_name:
            return Response({"error": "Test name is required"}, status=status.HTTP_400_BAD_REQUEST)
            
        test = MockTest.objects.create(name=test_name, is_free=is_free)
        
        for q in questions_data:
            Question.objects.create(
                test=test,
                question_text=q.get('question_text'),
                option1=q.get('option1'),
                option2=q.get('option2'),
                option3=q.get('option3'),
                option4=q.get('option4'),
                correct_option=q.get('correct_option'),
                section=q.get('section'),
                order=q.get('order', 0)
            )
            
        return Response({"message": "Test created successfully", "id": test.id}, status=status.HTTP_201_CREATED)
class AdminMockTestListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        
        tests = MockTest.objects.all().order_by('-id')
        total_count = tests.count()
        
        data = []
        for test in tests:
            data.append({
                "id": test.id,
                "name": test.name,
                "is_free": test.is_free,
                "question_count": test.questions.count(),
                "attempts_count": test.user_results.count()
            })
            
        return Response({
            "total_count": total_count,
            "tests": data
        })

    def patch(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        
        test_id = request.data.get('test_id')
        is_free = request.data.get('is_free')
        
        if test_id is None or is_free is None:
            return Response({"error": "test_id and is_free are required"}, status=status.HTTP_400_BAD_REQUEST)
            
        try:
            test = MockTest.objects.get(id=test_id)
            test.is_free = is_free
            test.save()
            return Response({"message": f"Test updated to {'Free' if is_free else 'Paid'}"})
        except MockTest.DoesNotExist:
            return Response({"error": "Test not found"}, status=status.HTTP_404_NOT_FOUND)

class PYQPaperView(views.APIView):
    def get_permissions(self):
        if self.request.method == 'GET':
            return [IsAuthenticated()]
        return [IsPlatformAdmin()]

    def get(self, request, pk=None):
        if pk:
            try:
                paper = PYQPaper.objects.get(pk=pk)
                serializer = PYQPaperSerializer(paper, context={'request': request})
                return Response(serializer.data)
            except PYQPaper.DoesNotExist:
                return Response({"error": "Not found"}, status=status.HTTP_404_NOT_FOUND)
        
        papers = PYQPaper.objects.all().order_by('-created_at')
        serializer = PYQPaperSerializer(papers, many=True, context={'request': request})
        return Response(serializer.data)

    def post(self, request, pk=None):
        serializer = PYQPaperSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk):
        try:
            paper = PYQPaper.objects.get(pk=pk)
            # Delete the file from storage
            if paper.file and os.path.exists(paper.file.path):
                os.remove(paper.file.path)
            paper.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)
        except PYQPaper.DoesNotExist:
            return Response({"error": "Not found"}, status=status.HTTP_404_NOT_FOUND)

class AdminGrantTestAccessView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        
        user_email = request.data.get('email')
        test_id = request.data.get('test_id')
        
        if not user_email or not test_id:
            return Response({"error": "email and test_id are required"}, status=status.HTTP_400_BAD_REQUEST)
            
        try:
            target_user = User.objects.get(email=user_email)
            test = MockTest.objects.get(id=test_id)
            
            from .models import UserUnlockedTest
            UserUnlockedTest.objects.get_or_create(user=target_user, test=test)
            
            return Response({"message": f"Successfully unlocked {test.name} for {target_user.email}"})
            
        except User.DoesNotExist:
            return Response({"error": "User not found"}, status=status.HTTP_404_NOT_FOUND)
        except MockTest.DoesNotExist:
            return Response({"error": "Mock test not found"}, status=status.HTTP_404_NOT_FOUND)

class AdminLockTestAccessView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        
        user_email = request.data.get('email')
        test_id = request.data.get('test_id')
        
        if not user_email or not test_id:
            return Response({"error": "email and test_id are required"}, status=status.HTTP_400_BAD_REQUEST)
            
        try:
            target_user = User.objects.get(email=user_email)
            test = MockTest.objects.get(id=test_id)
            
            from .models import UserUnlockedTest
            # Revoke access if it exists
            UserUnlockedTest.objects.filter(user=target_user, test=test).delete()
            
            return Response({"message": f"Successfully revoked access to {test.name} for {target_user.email}"})
            
        except User.DoesNotExist:
            return Response({"error": "User not found"}, status=status.HTTP_404_NOT_FOUND)
        except MockTest.DoesNotExist:
            return Response({"error": "Mock test not found"}, status=status.HTTP_404_NOT_FOUND)
