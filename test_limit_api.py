import requests
import json

BASE_URL = "http://localhost:8000/api"

def test_chat_limit(email, password):
    # 1. Login
    login_url = f"{BASE_URL}/auth/login/"
    login_data = {"email": email, "password": password}
    response = requests.post(login_url, json=login_data)
    
    if response.status_code != 200:
        print(f"Login failed: {response.status_code} {response.text}")
        return
    
    token = response.json()['access']
    headers = {"Authorization": f"Bearer {token}"}
    
    # 2. Check current count
    print(f"Testing chat limit for {email}...")
    
    # 3. Try sending messages until it hits 403 or we've sent too many
    for i in range(10):
        chat_url = f"{BASE_URL}/ai/chat/"
        chat_data = {"message": f"Test message {i+1}"}
        resp = requests.post(chat_url, json=chat_data, headers=headers)
        
        print(f"Message {i+1} status: {resp.status_code}")
        if resp.status_code == 403:
            print(f"Limit hit successfully! Response: {resp.text}")
            return
        elif resp.status_code != 201:
            print(f"Unexpected status: {resp.status_code} {resp.text}")
            return

if __name__ == "__main__":
    import sys
    if len(sys.argv) < 3:
        print("Usage: python test_limit_api.py <email> <password>")
    else:
        test_chat_limit(sys.argv[1], sys.argv[2])
