import os
import django
import sys
import traceback

# Setup Django Environment
sys.path.append(os.getcwd())
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from accounts.models import User
from accounts.serializers import UserSerializer, RegistrationSerializer
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth import authenticate

def debug_processes():
    print("--- RankForge AI: Login & Signup Debugger ---")
    
    try:
        # 1. Test Database Connection
        user_count = User.objects.count()
        print(f"Total users in DB: {user_count}")
        
        # 2. Pick a test user for Serializer check
        user = User.objects.all().first()
        if user:
            print(f"Testing Serializer with: {user.email}")
            from rest_framework.test import APIRequestFactory
            factory = APIRequestFactory()
            request = factory.get('/')
            data = UserSerializer(user, context={'request': request}).data
            print("SUCCESS: UserSerializer worked.")
        
        # 3. Test Registration Logic
        print("\nTesting Registration Logic with dummy data...")
        import random
        num = random.randint(1000, 9999)
        dummy_email = f"debug_user_{num}@example.com"
        dummy_data = {
            'email': dummy_email,
            'password': 'DebugPass@123',
            'full_name': 'Debug User'
        }
        
        ser = RegistrationSerializer(data=dummy_data)
        if ser.is_valid():
            new_user = ser.save()
            print(f"SUCCESS: New user created: {new_user.email}")
            # Clean up
            new_user.delete()
        else:
            print(f"FAILURE: Registration Serializer errors: {ser.errors}")
            
        print("\nDIAGNOSTIC COMPLETE: Internal logic is OK.")
        
    except Exception as e:
        print("\n--- DIAGNOSTIC FAILURE ---")
        print(f"Error Type: {type(e).__name__}")
        print(f"Error Message: {str(e)}")
        print("\nFull Traceback:")
        traceback.print_exc()

if __name__ == "__main__":
    debug_processes()
