from django.db import models
from django.contrib.auth.models import AbstractBaseUser, BaseUserManager

class UserManager(BaseUserManager):
    def create_user(self, email, password=None, **extra_fields):
        if not email:
            raise ValueError('The Email field must be set')
        email = self.normalize_email(email)
        user = self.model(email=email, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user


    def create_superuser(self, email, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('is_admin', True)

        return self.create_user(email, password, **extra_fields)

class User(AbstractBaseUser):
    GENDER_CHOICES = [
        ('Male', 'Male'),
        ('Female', 'Female'),
        ('Other', 'Other'),
    ]

    email = models.EmailField(unique=True)
    full_name = models.CharField(max_length=255, null=True, blank=True)
    phone_number = models.CharField(max_length=15, null=True, blank=True)
    dob = models.DateField(null=True, blank=True)
    city = models.CharField(max_length=100, null=True, blank=True)
    gender = models.CharField(max_length=10, choices=GENDER_CHOICES, null=True, blank=True)
    target_exam = models.CharField(max_length=100, null=True, blank=True)
    profile_picture = models.ImageField(upload_to='profiles/', null=True, blank=True)

    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    is_admin = models.BooleanField(default=False)
    is_superuser = models.BooleanField(default=False)
    
    # Premium features
    is_premium = models.BooleanField(default=False)
    premium_plan = models.CharField(max_length=50, null=True, blank=True)
    premium_expiry = models.DateTimeField(null=True, blank=True)
    
    # Referrals
    referral_code = models.CharField(max_length=10, unique=True, null=True, blank=True)

    # Settings Preferences
    push_notifications = models.BooleanField(default=False)
    study_reminders = models.BooleanField(default=False)
    test_alerts = models.BooleanField(default=False)
    community_updates = models.BooleanField(default=True)
    
    date_joined = models.DateTimeField(auto_now_add=True)

    objects = UserManager()

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = []

    @property
    def is_premium_active(self):
        from django.utils import timezone
        if self.is_premium and self.premium_expiry:
            return self.premium_expiry > timezone.now()
        return self.is_premium

    def __str__(self):
        return self.email

    def has_perm(self, perm, obj=None):
        return self.is_admin

    def has_module_perms(self, app_label):
        return True

    def save(self, *args, **kwargs):
        import uuid
        if not self.referral_code:
            self.referral_code = str(uuid.uuid4()).replace("-", "")[:8].upper()
        super().save(*args, **kwargs)

class Notification(models.Model):
    CATEGORY_CHOICES = [
        ('Tests', 'Tests'),
        ('Study', 'Study'),
        ('Community', 'Community'),
        ('Offers', 'Offers'),
        ('General', 'General'),
    ]

    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='notifications')
    title = models.CharField(max_length=255)
    message = models.TextField()
    category = models.CharField(max_length=50, choices=CATEGORY_CHOICES, default='General')
    is_read = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user.email} - {self.title}"

class GlobalNotification(models.Model):
    CATEGORY_CHOICES = [
        ('Tests', 'Tests'),
        ('Study', 'Study'),
        ('Community', 'Community'),
        ('Offers', 'Offers'),
        ('General', 'General'),
    ]
    title = models.CharField(max_length=255)
    message = models.TextField()
    category = models.CharField(max_length=50, choices=CATEGORY_CHOICES, default='General')
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.title

class DailyCheckIn(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='checkins')
    date = models.DateField(auto_now_add=True)

    class Meta:
        unique_together = ('user', 'date')

    def __str__(self):
        return f"{self.user.email} - {self.date}"

class ExamCategory(models.Model):
    name = models.CharField(max_length=100)
    tag_bg_color = models.CharField(max_length=20, default="#EAF1FF")
    icon_bg_color = models.CharField(max_length=20, default="#F0F2F5")
    icon_tint = models.CharField(max_length=20, default="#8C98A4")
    icon_name = models.CharField(max_length=50) # like 'ic_briefcase_blue'
    is_locked = models.BooleanField(default=True)
    order = models.IntegerField(default=0)

    def __str__(self):
        return self.name

class StudySubject(models.Model):
    category = models.ForeignKey(ExamCategory, on_delete=models.SET_NULL, related_name='subjects', null=True, blank=True)
    name = models.CharField(max_length=100)
    icon_name = models.CharField(max_length=50)
    icon_bg_drawable = models.CharField(max_length=100)
    progress_drawable = models.CharField(max_length=100)
    order = models.IntegerField(default=0)
    
    def __str__(self):
        return self.name

class StudyTopic(models.Model):
    subject = models.ForeignKey(StudySubject, on_delete=models.CASCADE, related_name='topics')
    name = models.CharField(max_length=200)
    theory = models.TextField(null=True, blank=True)
    formulas = models.TextField(null=True, blank=True)
    examples = models.JSONField(null=True, blank=True) # List of {question, solution}
    order = models.IntegerField(default=0)

    def __str__(self):
        return f"{self.subject.name} - {self.name}"

class UserTopicProgress(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='study_progress')
    topic = models.ForeignKey(StudyTopic, on_delete=models.CASCADE, related_name='user_progress')
    is_completed = models.BooleanField(default=False)

    class Meta:
        unique_together = ('user', 'topic')

    def __str__(self):
        return f"{self.user.email} - {self.topic.name}: {self.is_completed}"

class MockTest(models.Model):
    name = models.CharField(max_length=200)
    is_free = models.BooleanField(default=False)
    order = models.IntegerField(default=0)

    def __str__(self):
        return self.name

class Question(models.Model):
    test = models.ForeignKey(MockTest, on_delete=models.CASCADE, related_name='questions')
    question_text = models.TextField()
    option1 = models.CharField(max_length=255)
    option2 = models.CharField(max_length=255)
    option3 = models.CharField(max_length=255)
    option4 = models.CharField(max_length=255)
    correct_option = models.IntegerField() # 0 to 3
    section = models.CharField(max_length=50) # 'English', 'Intelligence', 'Quantitative', 'Awareness'
    order = models.IntegerField(default=0)

    def __str__(self):
        return f"{self.test.name} - Q{self.order}: {self.question_text[:30]}..."

class UserMockTestResult(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='mock_test_results')
    test = models.ForeignKey(MockTest, on_delete=models.CASCADE, related_name='user_results')
    score = models.IntegerField(default=0)
    total_questions = models.IntegerField(default=100)
    correct_count = models.IntegerField(default=0)
    incorrect_count = models.IntegerField(default=0)
    eng_score = models.IntegerField(default=0)
    quant_score = models.IntegerField(default=0)
    reason_score = models.IntegerField(default=0)
    gk_score = models.IntegerField(default=0)
    selected_answers = models.JSONField(null=True, blank=True)
    is_completed = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user.email} - {self.test.name} - Score: {self.score}"

class SupportTicket(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='support_tickets')
    message = models.TextField()
    admin_reply = models.TextField(null=True, blank=True)
    replied_at = models.DateTimeField(null=True, blank=True)
    is_resolved = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Ticket by {self.user.email} at {self.created_at} - {'Resolved' if self.is_resolved else 'Pending'}"

class PYQPaper(models.Model):
    title = models.CharField(max_length=255)
    file = models.FileField(upload_to='pyq_papers/')
    exam_category = models.CharField(max_length=100, null=True, blank=True)
    year = models.IntegerField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.title} ({self.year})"

class AiChatMessage(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='ai_messages')
    message = models.TextField()
    is_user = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['created_at']

    def __str__(self):
        role = "User" if self.is_user else "AI"
        return f"{role} at {self.created_at}: {self.message[:20]}..."

class UserUnlockedTest(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='unlocked_tests')
    test = models.ForeignKey(MockTest, on_delete=models.CASCADE)
    unlocked_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ('user', 'test')

    def __str__(self):
        return f"{self.user.email} unlocked {self.test.name}"

class SubscriptionPlan(models.Model):
    name = models.CharField(max_length=100)
    price = models.DecimalField(max_digits=10, decimal_places=2)
    description = models.TextField()
    duration_days = models.IntegerField(help_text="Duration of the plan in days")
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.name} - ₹{self.price}"
