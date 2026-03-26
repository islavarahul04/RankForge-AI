import requests

login_url = "http://127.0.0.1:8000/api/auth/login/"
test_url = "http://127.0.0.1:8000/api/tests/1/"

# Login to get token
resp = requests.post(login_url, json={"email": "demo@rankforge.ai", "password": "password"})
login_data = resp.json()
print("Login:", login_data)
token = login_data.get('tokens', {}).get('access')

if not token:
    print("Failed to get token!")
    exit(1)

headers = {"Authorization": f"Bearer {token}"}
resp = requests.get("http://127.0.0.1:8000/api/tests/", headers=headers)
tests = resp.json()
print("All Tests:", tests)

if tests:
    first_test_id = tests[0]['id']
    detail_resp = requests.get(f"http://127.0.0.1:8000/api/tests/{first_test_id}/", headers=headers)
    print("Test Detail:", detail_resp.json())
