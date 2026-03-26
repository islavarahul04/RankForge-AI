import json
import sqlite3
import os

db_path = "c:/Users/DELL/AndroidStudioProjects/RankForgeAI/backend/db.sqlite3"
json_path = "c:/Users/DELL/AndroidStudioProjects/RankForgeAI/app/src/main/assets/mock_test_1.json"

with open(json_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

conn = sqlite3.connect(db_path)
cur = conn.cursor()

# Get the test_id for Mock Test 1
cur.execute("SELECT id FROM accounts_mocktest ORDER BY id DESC LIMIT 1")
test_row = cur.fetchone()
if test_row:
    test_id = test_row[0]
    # Delete existing questions just in case
    cur.execute("DELETE FROM accounts_question WHERE test_id=?", (test_id,))
    
    order = 1
    for q in data['questions']:
        cur.execute("""
            INSERT INTO accounts_question 
            (test_id, question_text, option1, option2, option3, option4, correct_option, section, "order")
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (test_id, q['text'], q['a'], q['b'], q['c'], q['d'], q['correct'], q['section'], order))
        order += 1
    
    conn.commit()
    print(f"Successfully inserted {order-1} questions for test {test_id}!")
else:
    print("No mock test found in database!")

conn.close()
