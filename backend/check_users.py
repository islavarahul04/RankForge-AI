import os
import sys

sys.path.append(r'c:\Users\DELL\AndroidStudioProjects\RankForgeAI\backend')
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')

import django
django.setup()

from accounts.models import User
from django.db.models import Count

def check_duplicates():
    # 1. Check for multiple users with the same email (should be 0)
    email_duplicates = User.objects.values('email').annotate(count=Count('id')).filter(count__gt=1)
    print(f"Email duplicates (should be 0): {list(email_duplicates)}")

    # 2. Check for users with the same full_name
    name_duplicates = User.objects.values('full_name').annotate(count=Count('id')).filter(count__gt=1, full_name__isnull=False)
    print(f"Name duplicates found: {name_duplicates.count()}")
    for dup in name_duplicates:
        users = User.objects.filter(full_name=dup['full_name'])
        print(f"Name: {dup['full_name']} - Count: {dup['count']}")
        for u in users:
            print(f"  - ID: {u.id}, Email: {u.email}, Joined: {u.date_joined}")

    # 3. List all users to see if there are obvious patterns
    all_users = User.objects.filter(is_staff=False, is_superuser=False).order_by('full_name', 'email')
    print("\n--- All Standard Users ---")
    for u in all_users:
        print(f"ID: {u.id}, Name: {u.full_name}, Email: {u.email}")

if __name__ == "__main__":
    check_duplicates()
