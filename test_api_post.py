import requests

login_url = "http://127.0.0.1:8000/api/auth/admin-login/"
# Login to get token
resp = requests.post(login_url, json={"email": "demo@rankforge.ai", "password": "password"})
login_data = resp.json()
token = login_data.get('tokens', {}).get('access')
if not token:
    # Maybe use superuser
    resp = requests.post(login_url, json={"email": "admin@rankforge.ai", "password": "adminpassword"})
    token = resp.json().get('tokens', {}).get('access')

headers = {"Authorization": f"Bearer {token}"}

payload = {
    "name": "API Test",
    "is_free": True,
    "questions": [
        {
            "question_text": "Sample Q",
            "option1": "A",
            "option2": "B",
            "option3": "C",
            "option4": "D",
            "correct_option": 0,
            "section": "English",
            "order": 1
        }
    ]
}

resp = requests.post("http://127.0.0.1:8000/api/admin/tests/create/", json=payload, headers=headers)
print("Response:", resp.status_code, resp.json())
