from rest_framework import status, views
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth import authenticate, get_user_model
from django.core.cache import cache
import random
import traceback

from ..models import User
from ..serializers import UserSerializer, RegistrationSerializer

User = get_user_model()

class HealthCheckView(views.APIView):
    permission_classes = [AllowAny]
    def get(self, request):
        return Response({"status": "ok", "message": "API is reachable"})

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
                    # 'user': UserSerializer(user, context={'request': request}).data,
                    'refresh': str(refresh),
                    'access': str(refresh.access_token),
                })
            return Response({'error': 'Invalid Credentials'}, status=status.HTTP_401_UNAUTHORIZED)
        except Exception as e:
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
            print(f"Admin Login Error: {str(e)}")
            print(traceback.format_exc())
            return Response({'error': str(e), 'traceback': traceback.format_exc()}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

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
        cache.delete(f"otp_{user.email}")
        return Response({"message": "Password has been reset successfully."})
