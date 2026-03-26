import os
import sys
import django

# Setup Django environment
sys.path.append(r'c:\Users\DELL\AndroidStudioProjects\RankForgeAI\backend')
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from django.contrib.auth import authenticate, get_user_model

User = get_user_model()

email = "testuser@example.com"
password = "password123"

# Check if user exists
user = User.objects.filter(email=email).first()
if not user:
    print(f"User {email} not found. Creating one...")
    user = User.objects.create_user(email=email, password=password)
    print("User created.")
else:
    print(f"User {email} exists.")
    print(f"Active? {user.is_active}")
    print(f"Password starts with: {user.password[:15]}")

print("Testing raw check_password:")
print(user.check_password(password))

print("Testing authenticate:")
auth_user = authenticate(email=email, password=password)
print(f"Authenticated user: {auth_user}")
