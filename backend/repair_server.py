import os
import sys
import django
from django.core.management import call_command

# Setup Django
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(BASE_DIR)
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')

try:
    django.setup()
except Exception as e:
    print(f"CRITICAL: Django setup failed: {e}")
    sys.exit(1)

def repair():
    print("--- RankForge AI: Server Repair & Diagnostics ---")
    
    # 1. Check Dependencies
    print("\n[1/4] Checking Dependencies...")
    deps = [
        ('rest_framework_simplejwt', 'djangorestframework-simplejwt'),
        ('groq', 'groq'),
        ('PIL', 'Pillow'),
        ('corsheaders', 'django-cors-headers'),
        ('rest_framework', 'djangorestframework')
    ]
    
    all_ok = True
    for mod_name, pkg_name in deps:
        try:
            __import__(mod_name)
            print(f" [OK] {pkg_name} is installed.")
        except ImportError:
            print(f" [MISSING] {pkg_name} is NOT installed.")
            all_ok = False
            
    if not all_ok:
        print("\nPlease run: pip install -r requirements.txt")
        return

    # 2. Run Migrations
    print("\n[2/4] Checking Database Migrations...")
    try:
        call_command('migrate')
        print(" [OK] Migrations applied successfully.")
    except Exception as e:
        print(f" [ERROR] MIGRATION FAILED: {e}")
        return

    # 3. Verify Database Health
    print("\n[3/4] Verifying Database Health...")
    try:
        from django.contrib.auth import get_user_model
        User = get_user_model()
        user_count = User.objects.count()
        print(f" [OK] Database is reachable. Total users: {user_count}")
        if user_count == 0:
            print(" [!] WARNING: No users found. Run: python manage.py createsuperuser")
    except Exception as e:
        print(f" [ERROR] DATABASE ERROR: {e}")
        return

    # 4. Check Settings
    print("\n[4/4] Checking Settings...")
    from django.conf import settings
    if not settings.SECRET_KEY or 'your-secret-key' in settings.SECRET_KEY:
        print(" [!] WARNING: Secret key is default or missing!")
    else:
        print(" [OK] Secret key is set.")

    print("\n--- Repair Complete! ---")
    print("If you still see 500 errors, check your WSGI/Gunicorn logs.")
    print("Start server: python manage.py runserver 0.0.0.0:8124")

if __name__ == "__main__":
    repair()
