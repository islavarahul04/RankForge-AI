
import os
import django
import sys

# Add the backend directory to sys.path
sys.path.append(os.path.join(os.getcwd(), 'backend'))

# Set the Django settings module
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')

# Initialize Django
django.setup()

from accounts.models import User, DailyCheckIn, UserMockTestResult

def check_counts():
    print(f"Total Users: {User.objects.count()}")
    print(f"Staff Users: {User.objects.filter(is_staff=True).count()}")
    print(f"Platform Users: {User.objects.filter(is_staff=False, is_superuser=False).count()}")
    print(f"Daily Check-ins: {DailyCheckIn.objects.count()}")
    print(f"Mock Test Results: {UserMockTestResult.objects.count()}")
    print(f"Premium Users: {User.objects.filter(is_premium=True).count()}")

if __name__ == "__main__":
    check_counts()
