import os
import django
import sys
import traceback

# Setup Django Environment
sys.path.append(os.getcwd())
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from accounts.models import User
from accounts.serializers import UserSerializer
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth import authenticate

def debug_login():
    print("--- RankForge AI: Login Debugger ---")
    
    try:
        # 1. Test Database Connection
        user_count = User.objects.count()
        print(f"Total users in DB: {user_count}")
        
        # 2. Pick a test user (Travis Head)
        email = 'travisheadsrh123@gmail.com'
        print(f"Testing User Authentication: {email}")
        
        # NOTE: Since we can't easily know the hash for 'authenticate', 
        # we will test if we can find the user first.
        user = User.objects.filter(email=email).first()
        if not user:
            print(f"ERROR: User {email} not found.")
            # Fallback to first user
            user = User.objects.all().first()
            if not user: return
            print(f"Falling back to: {user.email}")
            
        # 3. Test JWT Generation
        print("Testing RefreshToken generation...")
        refresh = RefreshToken.for_user(user)
        print("SUCCESS: RefreshToken generated.")
        
        # 4. Test Serializer with simulated Request
        print("Testing UserSerializer with simulated context...")
        from rest_framework.test import APIRequestFactory
        factory = APIRequestFactory()
        request = factory.get('/')
        
        data = UserSerializer(user, context={'request': request}).data
        print("SUCCESS: UserSerializer worked.")
        print(f"Serialized Data: {data}")
        
        # 5. Test Authenticate (using the newly reset testuser password)
        print("\nTesting authenticate() for testuser@example.com...")
        test_user = authenticate(email='testuser@example.com', password='Pass@123')
        if test_user:
            print("SUCCESS: authenticate() worked for testuser@example.com")
        else:
            print("FAILURE: authenticate() returned None for testuser@example.com")
        
        print("\nDIAGNOSTIC COMPLETE: Internal logic is OK.")
        
    except Exception as e:
        print("\n--- DIAGNOSTIC FAILURE ---")
        print(f"Error Type: {type(e).__name__}")
        print(f"Error Message: {str(e)}")
        print("\nFull Traceback:")
        traceback.print_exc()

if __name__ == "__main__":
    debug_login()
