import os
import sys

def main():
    print("--- RankForge AI: Master Database Population Script ---")
    print("This will populate all subjects, topics, and mock tests.")
    
    # Run these from the backend directory
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    
    scripts = [
        "seed_study.py",
        "seed_mock_tests.py",
        "add_mock_tests_4_5.py",
        "populate_bulk_topics.py",
        "populate_batch_2.py"
    ]
    
    for script in scripts:
        print(f"\n>>> Running {script}...")
        result = os.system(f'"{sys.executable}" {script}')
        if result != 0:
            print(f" [ERROR] Failed to run {script}. Stopping.")
            return
            
    print("\n--- DONE! Database successfully populated! ---")
    print("You can now refresh the app and see all the content.")

if __name__ == "__main__":
    main()
