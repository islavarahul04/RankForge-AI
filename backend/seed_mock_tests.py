import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from accounts.models import MockTest

print("Generating mock tests...")

# Clear existing if any (prevent duplicates when dev testing)
MockTest.objects.all().delete()

# Test 1 is Free
m1 = MockTest.objects.create(name="Mock Test # 1", is_free=True, order=1)
# Test 2 & 3 are locked (is_free = False defaults)
m2 = MockTest.objects.create(name="Mock Test # 2", is_free=False, order=2)
m3 = MockTest.objects.create(name="Mock Test # 3", is_free=False, order=3)

print("Created:", m1, m2, m3)
print("Finished DB Sedding.")
