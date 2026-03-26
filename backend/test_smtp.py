import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from django.core.mail import send_mail
from django.conf import settings

def test_smtp_new_sender():
    print("--- Testing SMTP with New Sender (rankforgeaiapp@gmail.com) ---")
    recipient = "rankforgeaiapp@gmail.com" # Sending to self for test
    subject = "Final SMTP Verification - RankForge AI"
    message = "This is a final verification to confirm that emails are being sent from the new sender address correctly!"
    
    try:
        print(f"Sending test email to {recipient}...")
        send_mail(
            subject,
            message,
            settings.DEFAULT_FROM_EMAIL,
            [recipient],
            fail_silently=False,
        )
        print("SUCCESS: Email sent successfully!")
    except Exception as e:
        print(f"FAILURE: {str(e)}")

if __name__ == "__main__":
    test_smtp_new_sender()
