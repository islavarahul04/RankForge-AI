import os
import sys
import django
from django.core.management import call_command

# Setup Django
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(BASE_DIR)
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

def repair():
    print("--- RankForge AI: Server Repair & Diagnostics ---")
    
    # 1. Check Dependencies
    print("\n[1/3] Checking Dependencies...")
    try:
        import rest_framework_simplejwt
        import groq
        import PIL
        print("✓ Core dependencies (SimpleJWT, Groq, Pillow) are installed.")
    except ImportError as e:
        print(f"✗ MISSING DEPENDENCY: {e}")
        print("Please run: pip install -r requirements.txt")
        return

    # 2. Run Migrations
    print("\n[2/3] Checking Database Migrations...")
    try:
        call_command('migrate')
        print("✓ Migrations applied successfully.")
    except Exception as e:
        print(f"✗ MIGRATION ERROR: {e}")
        return

    # 3. Verify Database Health
    print("\n[3/3] Verifying Database Health...")
    from django.contrib.auth import get_user_model
    User = get_user_model()
    user_count = User.objects.count()
    print(f"✓ Database is reachable. Total users: {user_count}")
    
    if user_count == 0:
        print("! WARNING: No users found. You should create a superuser.")
    
    print("\n--- Repair Complete! ---")
    print("Please restart your server using: python manage.py runserver 0.0.0.0:8124")

if __name__ == "__main__":
    repair()
