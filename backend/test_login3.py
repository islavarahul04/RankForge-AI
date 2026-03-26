import os
import sys
import django

sys.path.append(r'c:\Users\DELL\AndroidStudioProjects\RankForgeAI\backend')
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from django.contrib.auth import authenticate, get_user_model
User = get_user_model()

email = "testlogin@example.com"
password = "mypassword123"

# Cleanup old test
User.objects.filter(email=email).delete()

# Create new user
user = User.objects.create_user(email=email, password=password)
print(f"Created user: {user.email}")
print(f"Password starts with: {user.password[:15]}")

auth_user = authenticate(email=email, password=password)
print(f"Authenticate result for {email} with {password}: {auth_user}")
