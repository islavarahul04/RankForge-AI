import sqlite3
import os

db_path = "c:/Users/DELL/AndroidStudioProjects/RankForgeAI/backend/db.sqlite3"
conn = sqlite3.connect(db_path)
cur = conn.cursor()
cur.execute("SELECT id, name FROM accounts_mocktest")
tests = cur.fetchall()
print("Tests:", tests)

for t in tests:
    test_id = t[0]
    cur.execute("SELECT COUNT(*) FROM accounts_question WHERE test_id=?", (test_id,))
    count = cur.fetchone()[0]
    print(f"Test {test_id} '{t[1]}' has {count} questions.")

cur.execute("SELECT * FROM django_admin_log ORDER BY id DESC LIMIT 5")
print("Logs:", cur.fetchall())

conn.close()
