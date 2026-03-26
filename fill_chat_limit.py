import os
import django
import sys
from datetime import date

# Set up Django environment
sys.path.append(os.path.join(os.getcwd(), 'backend'))
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from accounts.models import User, AiChatMessage

def fill_chats(email):
    try:
        user = User.objects.get(email=email)
        # Count existing user messages today
        today = date.today()
        count = AiChatMessage.objects.filter(user=user, is_user=True, created_at__date=today).count()
        
        needed = 20 - count
        if needed <= 0:
            print(f"User {email} already has {count} messages today. Limit reached.")
            return

        print(f"Adding {needed} messages for {email} to hit the limit...")
        for i in range(needed):
            AiChatMessage.objects.create(
                user=user,
                message=f"Auto-generated test message {i+1}",
                is_user=True
            )
            AiChatMessage.objects.create(
                user=user,
                message=f"AI response {i+1}",
                is_user=False
            )
        print("Done! Next chat attempt should trigger the 'Limit Exhausted' popup.")
        
    except User.DoesNotExist:
        print(f"User with email {email} not found.")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python fill_chat_limit.py <user_email>")
    else:
        fill_chats(sys.argv[1])
