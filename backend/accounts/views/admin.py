from rest_framework import views, viewsets, status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, IsAdminUser
from django.db.models import Count, Avg
from datetime import date, timedelta
import os

from ..models import (User, Notification, GlobalNotification, UserMockTestResult, 
                    MockTest, Question, SupportTicket, PYQPaper, SubscriptionPlan, 
                    UserUnlockedTest, DailyCheckIn)
from ..serializers import (UserSerializer, NotificationSerializer, SupportTicketSerializer, 
                         PYQPaperSerializer, SubscriptionPlanSerializer)
from .permissions import IsPlatformAdmin

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

        total_users = User.objects.filter(is_staff=False, is_superuser=False).count()
        users_yesterday = User.objects.filter(is_staff=False, is_superuser=False, date_joined__lt=today).count()
        users_percentage = calculate_percentage(total_users, users_yesterday)

        active_today = DailyCheckIn.objects.filter(date=today).values('user').distinct().count()
        active_yesterday = DailyCheckIn.objects.filter(date=yesterday).values('user').distinct().count()
        active_percentage = calculate_percentage(active_today, active_yesterday)

        premium_users = User.objects.filter(is_premium=True).count()
        revenue = premium_users * 999
        premium_users_yesterday = User.objects.filter(is_premium=True, date_joined__lt=today).count()
        revenue_yesterday = premium_users_yesterday * 999
        revenue_percentage = calculate_percentage(revenue, revenue_yesterday)

        tests_taken_today = UserMockTestResult.objects.filter(created_at__gte=today).count()
        tests_taken_yesterday = UserMockTestResult.objects.filter(created_at__gte=yesterday, created_at__lt=today).count()
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

        GlobalNotification.objects.create(title=title, message=message, category=category)
        users = User.objects.filter(is_staff=False, is_superuser=False)
        notifications = [Notification(user=user, title=title, message=message, category=category) for user in users]
        Notification.objects.bulk_create(notifications)
        return Response({'message': f'Successfully broadcasted to {len(notifications)} users.'})

class AdminUserListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)

        users_list = []
        users = User.objects.filter(is_staff=False, is_superuser=False)
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

class AdminSupportTicketView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        tickets = SupportTicket.objects.all().order_by('-created_at')
        data = [{"id": t.id, "user_email": t.user.email, "user_name": t.user.full_name or "Anonymous", "message": t.message, "admin_reply": t.admin_reply, "replied_at": t.replied_at, "is_resolved": t.is_resolved, "created_at": t.created_at} for t in tickets]
        return Response(data)

    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        ticket_id = request.data.get('ticket_id')
        reply = request.data.get('reply')
        is_resolved = request.data.get('is_resolved', True)
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

class AdminMockTestReportView(views.APIView):
    permission_classes = [IsAuthenticated]
    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        reports = MockTest.objects.annotate(attempts_count=Count('user_results'), average_score=Avg('user_results__score')).filter(attempts_count__gt=0).order_by('-attempts_count')
        data = [{"id": r.id, "name": r.name, "attempts_count": r.attempts_count, "average_score": round(r.average_score, 2) if r.average_score else 0} for r in reports]
        return Response(data)

class AdminTestResultsDetailView(views.APIView):
    permission_classes = [IsAuthenticated]
    def get(self, request, test_id):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin):
            return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        try:
            test = MockTest.objects.get(id=test_id)
            results = UserMockTestResult.objects.filter(test=test).select_related('user').order_by('-score')
            data = [{"userName": res.user.full_name or "Anonymous User", "email": res.user.email, "score": res.score, "completed": res.is_completed, "date": res.created_at.strftime('%Y-%m-%d %H:%M')} for res in results]
            return Response({"test_name": test.name, "results": data})
        except MockTest.DoesNotExist:
            return Response({"error": "Mock test not found"}, status=404)

class AdminCreateTestView(views.APIView):
    permission_classes = [IsAuthenticated]
    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin): return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        test_name = request.data.get('name')
        is_free = request.data.get('is_free', False)
        questions_data = request.data.get('questions', [])
        if not test_name: return Response({"error": "Test name is required"}, status=status.HTTP_400_BAD_REQUEST)
        test = MockTest.objects.create(name=test_name, is_free=is_free)
        for q in questions_data: Question.objects.create(test=test, question_text=q.get('question_text'), option1=q.get('option1'), option2=q.get('option2'), option3=q.get('option3'), option4=q.get('option4'), correct_option=q.get('correct_option'), section=q.get('section'), order=q.get('order', 0))
        return Response({"message": "Test created successfully", "id": test.id}, status=status.HTTP_201_CREATED)

class AdminMockTestListView(views.APIView):
    permission_classes = [IsAuthenticated]
    def get(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin): return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        tests = MockTest.objects.all().order_by('-id')
        data = [{"id": t.id, "name": t.name, "is_free": t.is_free, "question_count": t.questions.count(), "attempts_count": t.user_results.count()} for t in tests]
        return Response({"total_count": tests.count(), "tests": data})
    def patch(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin): return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        test_id, is_free = request.data.get('test_id'), request.data.get('is_free')
        if test_id is None or is_free is None: return Response({"error": "test_id and is_free are required"}, status=status.HTTP_400_BAD_REQUEST)
        try:
            test = MockTest.objects.get(id=test_id)
            test.is_free = is_free
            test.save()
            return Response({"message": f"Test updated to {'Free' if is_free else 'Paid'}"})
        except MockTest.DoesNotExist: return Response({"error": "Test not found"}, status=status.HTTP_404_NOT_FOUND)

class PYQPaperView(views.APIView):
    def get_permissions(self):
        return [IsAuthenticated()] if self.request.method == 'GET' else [IsPlatformAdmin()]
    def get(self, request, pk=None):
        if pk:
            try:
                paper = PYQPaper.objects.get(pk=pk)
                return Response(PYQPaperSerializer(paper, context={'request': request}).data)
            except PYQPaper.DoesNotExist: return Response({"error": "Not found"}, status=status.HTTP_404_NOT_FOUND)
        papers = PYQPaper.objects.all().order_by('-created_at')
        return Response(PYQPaperSerializer(papers, many=True, context={'request': request}).data)
    def post(self, request, pk=None):
        serializer = PYQPaperSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    def delete(self, request, pk):
        try:
            paper = PYQPaper.objects.get(pk=pk)
            if paper.file and os.path.exists(paper.file.path): os.remove(paper.file.path)
            paper.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)
        except PYQPaper.DoesNotExist: return Response({"error": "Not found"}, status=status.HTTP_404_NOT_FOUND)

class AdminGrantTestAccessView(views.APIView):
    permission_classes = [IsAuthenticated]
    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin): return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        email, test_id = request.data.get('email'), request.data.get('test_id')
        if not email or not test_id: return Response({"error": "email and test_id are required"}, status=status.HTTP_400_BAD_REQUEST)
        try:
            user = User.objects.get(email=email)
            test = MockTest.objects.get(id=test_id)
            UserUnlockedTest.objects.get_or_create(user=user, test=test)
            return Response({"message": f"Successfully unlocked {test.name} for {user.email}"})
        except User.DoesNotExist: return Response({"error": "User not found"}, status=status.HTTP_404_NOT_FOUND)
        except MockTest.DoesNotExist: return Response({"error": "Mock test not found"}, status=status.HTTP_404_NOT_FOUND)

class AdminLockTestAccessView(views.APIView):
    permission_classes = [IsAuthenticated]
    def post(self, request):
        if not (request.user.is_staff or request.user.is_superuser or request.user.is_admin): return Response({'error': 'Unauthorized'}, status=status.HTTP_403_FORBIDDEN)
        email, test_id = request.data.get('email'), request.data.get('test_id')
        if not email or not test_id: return Response({"error": "email and test_id are required"}, status=status.HTTP_400_BAD_REQUEST)
        try:
            user = User.objects.get(email=email)
            test = MockTest.objects.get(id=test_id)
            UserUnlockedTest.objects.filter(user=user, test=test).delete()
            return Response({"message": f"Successfully revoked access to {test.name} for {user.email}"})
        except Exception: return Response({"error": "Not found"}, status=status.HTTP_404_NOT_FOUND)

class AdminSubscriptionPlanViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAdminUser]
    queryset = SubscriptionPlan.objects.all().order_by('price')
    serializer_class = SubscriptionPlanSerializer
