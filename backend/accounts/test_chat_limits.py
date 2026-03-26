from django.test import TestCase
from django.contrib.auth import get_user_model
from rest_framework.test import APIClient
from rest_framework import status
from accounts.models import AiChatMessage
from django.utils import timezone

User = get_user_model()

class AiChatLimitsTestCase(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.basic_user = User.objects.create_user(email="basic@example.com", password="password")
        self.premium_user = User.objects.create_user(email="premium@example.com", password="password", is_premium=True)

    def test_basic_user_limit(self):
        self.client.force_authenticate(user=self.basic_user)
        
        # Send 20 messages (should succeed)
        for i in range(20):
            response = self.client.post('/api/ai/chat/', {'message': f'Test message {i+1}'})
            self.assertEqual(response.status_code, status.HTTP_201_CREATED)
            
        # 21st message (should fail with 403 limit_exhausted)
        response = self.client.post('/api/ai/chat/', {'message': '21st message'})
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)
        self.assertEqual(response.data['error'], 'limit_exhausted')

    def test_premium_user_no_limit(self):
        self.client.force_authenticate(user=self.premium_user)
        
        # Send 7 messages (should all succeed)
        for i in range(7):
            response = self.client.post('/api/ai/chat/', {'message': f'Premium message {i+1}'})
            self.assertEqual(response.status_code, status.HTTP_201_CREATED)

    def test_limit_resets_next_day(self):
        self.client.force_authenticate(user=self.basic_user)
        
        # Create 20 messages from yesterday
        yesterday = timezone.now() - timezone.timedelta(days=1)
        for i in range(20):
            AiChatMessage.objects.create(user=self.basic_user, message=f"Old message {i}", is_user=True, created_at=yesterday)
            # Hack to set created_at as it's auto_now_add
            AiChatMessage.objects.filter(message=f"Old message {i}").update(created_at=yesterday)

        # Should be able to send 20 messages today
        for i in range(20):
            response = self.client.post('/api/ai/chat/', {'message': f'Today message {i+1}'})
            self.assertEqual(response.status_code, status.HTTP_201_CREATED)
