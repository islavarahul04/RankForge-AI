from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import User, Notification, ExamCategory, StudySubject, StudyTopic, UserTopicProgress, MockTest, UserMockTestResult, PYQPaper, SubscriptionPlan

User = get_user_model()

class UserSerializer(serializers.ModelSerializer):
    tests_attempted = serializers.SerializerMethodField()
    average_accuracy = serializers.SerializerMethodField()
    is_premium = serializers.ReadOnlyField(source='is_premium_active')

    class Meta:
        model = User
        fields = ('id', 'email', 'full_name', 'phone_number', 'dob', 'city', 'gender', 'target_exam', 'profile_picture', 'is_premium', 'premium_plan', 'premium_expiry', 'referral_code', 'push_notifications', 'study_reminders', 'test_alerts', 'community_updates', 'tests_attempted', 'average_accuracy', 'is_staff', 'is_admin', 'is_superuser')
        read_only_fields = ('id', 'email', 'referral_code')

    def get_tests_attempted(self, obj):
        return UserMockTestResult.objects.filter(user=obj, is_completed=True).count()

    def get_average_accuracy(self, obj):
        results = UserMockTestResult.objects.filter(user=obj, is_completed=True)
        if not results.exists():
            return 0
        total_percentage = sum([(res.score / (res.total_questions * 2)) * 100 for res in results if res.total_questions > 0])
        return round(total_percentage / results.count(), 1)


class RegistrationSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)

    class Meta:
        model = User
        fields = ('email', 'password', 'full_name')

    def validate_password(self, value):
        if len(value) < 8:
            raise serializers.ValidationError("Password must be at least 8 characters long.")
        if not any(char.isdigit() for char in value):
            raise serializers.ValidationError("Password must contain at least one number.")
        if not any(char.isupper() for char in value):
            raise serializers.ValidationError("Password must contain at least one uppercase letter.")
        import re
        if not re.search(r'[!@#$%^&*(),.?":{}|<>]', value):
            raise serializers.ValidationError("Password must contain at least one special character.")
        return value

    def create(self, validated_data):
        user = User.objects.create_user(
            email=validated_data['email'],
            password=validated_data['password'],
            full_name=validated_data.get('full_name', '')
        )
        return user

class NotificationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Notification
        fields = ('id', 'title', 'message', 'category', 'is_read', 'created_at')
        read_only_fields = ['user', 'created_at']

class ExamCategorySerializer(serializers.ModelSerializer):
    class Meta:
        model = ExamCategory
        fields = ['id', 'name', 'tag_bg_color', 'icon_bg_color', 'icon_tint', 'icon_name', 'is_locked', 'order']

class StudyTopicSerializer(serializers.ModelSerializer):
    is_completed = serializers.SerializerMethodField()

    class Meta:
        model = StudyTopic
        fields = ['id', 'name', 'theory', 'formulas', 'examples', 'order', 'is_completed']

    def get_is_completed(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            progress = UserTopicProgress.objects.filter(user=request.user, topic=obj).first()
            return progress.is_completed if progress else False
        return False

class StudySubjectSerializer(serializers.ModelSerializer):
    topics = StudyTopicSerializer(many=True, read_only=True)

    class Meta:
        model = StudySubject
        fields = ['id', 'name', 'icon_name', 'icon_bg_drawable', 'progress_drawable', 'order', 'topics']

from .models import Question

class QuestionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Question
        fields = ['id', 'question_text', 'option1', 'option2', 'option3', 'option4', 'correct_option', 'section', 'order']

class MockTestListSerializer(serializers.ModelSerializer):
    is_completed = serializers.SerializerMethodField()
    highest_score = serializers.SerializerMethodField()
    latest_score = serializers.SerializerMethodField()
    question_count = serializers.SerializerMethodField()
    is_unlocked_manually = serializers.SerializerMethodField()

    class Meta:
        model = MockTest
        fields = ['id', 'name', 'is_free', 'order', 'is_completed', 'highest_score', 'latest_score', 'question_count', 'is_unlocked_manually']

    def get_is_unlocked_manually(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            from .models import UserUnlockedTest
            return UserUnlockedTest.objects.filter(user=request.user, test=obj).exists()
        return False

    def get_latest_score(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            result = UserMockTestResult.objects.filter(user=request.user, test=obj).order_by('-created_at').first()
            return result.score if result else 0
        return 0

    def get_question_count(self, obj):
        return obj.questions.count()

    def get_is_completed(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return UserMockTestResult.objects.filter(user=request.user, test=obj, is_completed=True).exists()
        return False

    def get_highest_score(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            result = UserMockTestResult.objects.filter(user=request.user, test=obj).order_by('-score').first()
            return result.score if result else 0
        return 0

class MockTestSerializer(serializers.ModelSerializer):
    is_completed = serializers.SerializerMethodField()
    highest_score = serializers.SerializerMethodField()
    latest_score = serializers.SerializerMethodField()
    questions = QuestionSerializer(many=True, read_only=True)
    is_unlocked_manually = serializers.SerializerMethodField()

    class Meta:
        model = MockTest
        fields = ['id', 'name', 'is_free', 'order', 'is_completed', 'highest_score', 'latest_score', 'questions', 'is_unlocked_manually']

    def get_is_unlocked_manually(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            from .models import UserUnlockedTest
            return UserUnlockedTest.objects.filter(user=request.user, test=obj).exists()
        return False

    def get_is_completed(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return UserMockTestResult.objects.filter(user=request.user, test=obj, is_completed=True).exists()
        return False

    def get_highest_score(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            result = UserMockTestResult.objects.filter(user=request.user, test=obj).order_by('-score').first()
            return result.score if result else 0
        return 0

    def get_latest_score(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            result = UserMockTestResult.objects.filter(user=request.user, test=obj).order_by('-created_at').first()
            return result.score if result else 0
        return 0

class UserMockTestResultSerializer(serializers.ModelSerializer):
    test_name = serializers.CharField(source='test.name', read_only=True)
    test_id = serializers.IntegerField(source='test.id', read_only=True)
    
    class Meta:
        model = UserMockTestResult
        fields = ['id', 'test_id', 'test_name', 'score', 'total_questions', 'correct_count', 'incorrect_count', 'eng_score', 'quant_score', 'reason_score', 'gk_score', 'selected_answers', 'is_completed', 'created_at']

class PYQPaperSerializer(serializers.ModelSerializer):
    class Meta:
        model = PYQPaper
        fields = '__all__'

from .models import SupportTicket

class SupportTicketSerializer(serializers.ModelSerializer):
    class Meta:
        model = SupportTicket
        fields = ['id', 'message', 'admin_reply', 'replied_at', 'is_resolved', 'created_at']
        read_only_fields = ['id', 'admin_reply', 'replied_at', 'created_at']

from .models import AiChatMessage

class AiChatMessageSerializer(serializers.ModelSerializer):
    class Meta:
        model = AiChatMessage
        fields = ['id', 'message', 'is_user', 'created_at']
        read_only_fields = ['id', 'created_at']

class SubscriptionPlanSerializer(serializers.ModelSerializer):
    class Meta:
        model = SubscriptionPlan
        fields = '__all__'
