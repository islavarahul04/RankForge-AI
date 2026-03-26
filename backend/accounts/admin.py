from django.contrib import admin
from .models import (User, Notification, DailyCheckIn, ExamCategory, 
                     StudySubject, StudyTopic, UserTopicProgress, 
                     MockTest, UserMockTestResult)

admin.site.register(User)
admin.site.register(Notification)
admin.site.register(DailyCheckIn)
admin.site.register(ExamCategory)
admin.site.register(StudySubject)
admin.site.register(StudyTopic)
admin.site.register(UserTopicProgress)
admin.site.register(MockTest)
admin.site.register(UserMockTestResult)
