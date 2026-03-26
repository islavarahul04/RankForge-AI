from django.urls import path
from .views import (RegisterView, LoginView, AdminLoginView, AdminDashboardStatsView, AdminUserListView, ForgotPasswordView, VerifyOTPView, ResetPasswordView, AiChatView, ProfileView, NotificationListView, 
                    StreakView, ExamCategoryListView, StudySubjectListView, TopicProgressUpdateView,
                    MockTestListView, SubmitMockTestResultView, SubscribeView, TestHistoryView, SupportTicketView, AdminSupportTicketView, ChangePasswordView,
                     AdminMockTestReportView, AdminTestResultsDetailView, AdminNotificationCreateView,
                     MockTestDetailView, AdminCreateTestView, AdminMockTestListView, PYQPaperView, AdminGrantTestAccessView, AdminLockTestAccessView,
                     SubscriptionPlanListView, AdminSubscriptionPlanViewSet,
                     AdminExamCategoryViewSet, AdminStudySubjectViewSet, AdminStudyTopicViewSet)

from rest_framework.routers import DefaultRouter

router = DefaultRouter()
router.register(r'admin/subscriptions', AdminSubscriptionPlanViewSet, basename='admin_subscriptions')
router.register(r'admin/study/exams', AdminExamCategoryViewSet, basename='admin_study_exams')
router.register(r'admin/study/subjects', AdminStudySubjectViewSet, basename='admin_study_subjects')
router.register(r'admin/study/topics', AdminStudyTopicViewSet, basename='admin_study_topics')
urlpatterns = [
    path('auth/register/', RegisterView.as_view(), name='register'),
    path('auth/login/', LoginView.as_view(), name='login'),
    path('admin/login/', AdminLoginView.as_view(), name='admin_login'),
    path('auth/admin-login/', AdminLoginView.as_view(), name='admin_login_legacy'),
    path('admin/dashboard-stats/', AdminDashboardStatsView.as_view(), name='admin_dashboard_stats'),
    path('admin/users/', AdminUserListView.as_view(), name='admin_users'),
    path('admin/users/unlock-test/', AdminGrantTestAccessView.as_view(), name='admin-grant-test-access'),
    path('admin/users/lock-test/', AdminLockTestAccessView.as_view(), name='admin-lock-test-access'),
    path('admin/notifications/create/', AdminNotificationCreateView.as_view(), name='admin_create_notification'),
    path('admin/reports/mock-tests/', AdminMockTestReportView.as_view(), name='admin_mock_test_reports'),
    path('admin/reports/test-details/<int:test_id>/', AdminTestResultsDetailView.as_view(), name='admin_test_results_detail'),
    path('admin/tests/create/', AdminCreateTestView.as_view(), name='admin_create_test'),
    path('admin/tests/list/', AdminMockTestListView.as_view(), name='admin_mock_tests_list'),
    path('auth/forgot-password/', ForgotPasswordView.as_view(), name='forgot_password'),
    path('auth/verify-otp/', VerifyOTPView.as_view(), name='verify_otp'),
    path('auth/reset-password/', ResetPasswordView.as_view(), name='reset_password'),
    path('ai/chat/', AiChatView.as_view(), name='ai_chat'),
    path('profile/', ProfileView.as_view(), name='profile'),
    path('profile/password/', ChangePasswordView.as_view(), name='change_password'),
    path('user/subscribe/', SubscribeView.as_view(), name='subscribe'),
    path('notifications/', NotificationListView.as_view(), name='notifications'),
    path('streak/', StreakView.as_view(), name='streak'),
    path('exams/categories/', ExamCategoryListView.as_view(), name='exam_categories'),
    path('study/subjects/', StudySubjectListView.as_view(), name='study_subjects'),
    path('study/progress/', TopicProgressUpdateView.as_view(), name='topic_progress'),
    path('tests/', MockTestListView.as_view(), name='mock_tests'),
    path('tests/<int:test_id>/', MockTestDetailView.as_view(), name='mock_test_detail'),
    path('tests/submit/', SubmitMockTestResultView.as_view(), name='submit_mock_test'),
    path('tests/history/', TestHistoryView.as_view(), name='test_history'),
    path('support/', SupportTicketView.as_view(), name='support_ticket'),
    path('admin/support/manage/', AdminSupportTicketView.as_view(), name='admin_support_manage'),
    path('pyq-papers/', PYQPaperView.as_view(), name='pyq_papers'),
    path('pyq-papers/<int:pk>/', PYQPaperView.as_view(), name='pyq_paper_detail'),
    path('subscriptions/', SubscriptionPlanListView.as_view(), name='subscription_plans'),
] + router.urls
