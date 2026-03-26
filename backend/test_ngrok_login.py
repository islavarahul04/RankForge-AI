import requests

url = "https://kindra-venulose-innoxiously.ngrok-free.dev/api/auth/login/"
data = {
    "email": "test@example.com",
    "password": "password123"
}
headers = {
    "ngrok-skip-browser-warning": "69420"
}

try:
    response = requests.post(url, json=data, headers=headers)
    print(f"Status Code: {response.status_code}")
    print(f"Response Body: {response.text}")
except Exception as e:
    print(f"Error: {e}")
