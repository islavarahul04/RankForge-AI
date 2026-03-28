from django.urls import path
from rest_framework.routers import DefaultRouter
from .views import auth, admin, profile, gamification, study, tests, ai, support, billing

router = DefaultRouter()
router.register(r'admin/subscriptions', admin.AdminSubscriptionPlanViewSet, basename='admin_subscriptions')
router.register(r'admin/study/exams', study.AdminExamCategoryViewSet, basename='admin_study_exams')
router.register(r'admin/study/subjects', study.AdminStudySubjectViewSet, basename='admin_study_subjects')
router.register(r'admin/study/topics', study.AdminStudyTopicViewSet, basename='admin_study_topics')

urlpatterns = [
    # Auth
    path('health/', auth.HealthCheckView.as_view(), name='health_check'),
    path('auth/register/', auth.RegisterView.as_view(), name='register'),
    path('auth/login/', auth.LoginView.as_view(), name='login'),
    path('admin/login/', auth.AdminLoginView.as_view(), name='admin_login'),
    path('auth/admin-login/', auth.AdminLoginView.as_view(), name='admin_login_legacy'),
    path('auth/forgot-password/', auth.ForgotPasswordView.as_view(), name='forgot_password'),
    path('auth/verify-otp/', auth.VerifyOTPView.as_view(), name='verify_otp'),
    path('auth/reset-password/', auth.ResetPasswordView.as_view(), name='reset_password'),

    # Profile & Billing
    path('profile/', profile.ProfileView.as_view(), name='profile'),
    path('profile/password/', profile.ChangePasswordView.as_view(), name='change_password'),
    path('subscriptions/', billing.SubscriptionPlanListView.as_view(), name='subscription_plans'),
    path('user/subscribe/', billing.SubscribeView.as_view(), name='subscribe'),

    # Gamification & AI
    path('streak/', gamification.StreakView.as_view(), name='streak'),
    path('notifications/', gamification.NotificationListView.as_view(), name='notifications'),
    path('ai/chat/', ai.AiChatView.as_view(), name='ai_chat'),

    # Study & Tests
    path('exams/categories/', study.ExamCategoryListView.as_view(), name='exam_categories'),
    path('study/subjects/', study.StudySubjectListView.as_view(), name='study_subjects'),
    path('study/progress/', study.TopicProgressUpdateView.as_view(), name='topic_progress'),
    path('tests/', tests.MockTestListView.as_view(), name='mock_tests'),
    path('tests/<int:test_id>/', tests.MockTestDetailView.as_view(), name='mock_test_detail'),
    path('tests/submit/', tests.SubmitMockTestResultView.as_view(), name='submit_mock_test'),
    path('tests/history/', tests.TestHistoryView.as_view(), name='test_history'),

    # Support
    path('support/', support.SupportTicketView.as_view(), name='support_ticket'),

    # Admin
    path('admin/dashboard-stats/', admin.AdminDashboardStatsView.as_view(), name='admin_dashboard_stats'),
    path('admin/users/', admin.AdminUserListView.as_view(), name='admin_users'),
    path('admin/users/unlock-test/', admin.AdminGrantTestAccessView.as_view(), name='admin-grant-test-access'),
    path('admin/users/lock-test/', admin.AdminLockTestAccessView.as_view(), name='admin-lock-test-access'),
    path('admin/notifications/create/', admin.AdminNotificationCreateView.as_view(), name='admin_create_notification'),
    path('admin/reports/mock-tests/', admin.AdminMockTestReportView.as_view(), name='admin_mock_test_reports'),
    path('admin/reports/test-details/<int:test_id>/', admin.AdminTestResultsDetailView.as_view(), name='admin_test_results_detail'),
    path('admin/tests/create/', admin.AdminCreateTestView.as_view(), name='admin_create_test'),
    path('admin/tests/list/', admin.AdminMockTestListView.as_view(), name='admin_mock_tests_list'),
    path('admin/support/manage/', admin.AdminSupportTicketView.as_view(), name='admin_support_manage'),
    path('pyq-papers/', admin.PYQPaperView.as_view(), name='pyq_papers'),
    path('pyq-papers/<int:pk>/', admin.PYQPaperView.as_view(), name='pyq_paper_detail'),

] + router.urls
