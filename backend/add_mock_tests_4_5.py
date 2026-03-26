import os
import django
import random

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from accounts.models import MockTest, Question

def add_mock_tests():
    print("Starting Mock Test 4 & 5 Creation...")
    
    # Check if they already exist to avoid duplicates
    mt4, created4 = MockTest.objects.get_or_create(name="SSC CHSL Mock Test 4", defaults={'is_free': False, 'order': 4})
    mt5, created5 = MockTest.objects.get_or_create(name="SSC CHSL Mock Test 5", defaults={'is_free': False, 'order': 5})
    
    if created4: print("Created Mock Test 4")
    else: print("Mock Test 4 already exists")
    
    if created5: print("Created Mock Test 5")
    else: print("Mock Test 5 already exists")

    # Question Pool Templates
    pool = {
        'English': [
            ("Select the synonym of 'Frugal'", "Economical", "Spendthrift", "Generous", "Wasteful", 0),
            ("Identify the correctly spelled word", "Accommodation", "Acomodation", "Accomodation", "Acommodation", 0),
            ("Change to passive: 'She wrote a letter'", "A letter is written by her", "A letter was written by her", "A letter had been written by her", "A letter was wrote by her", 1),
            ("Select the antonym of 'Vague'", "Unclear", "Sharp", "Precise", "Dull", 2),
            ("One word substitution: 'A person who loves books'", "Philatelist", "Bibliophile", "Optimist", "Misanthrope", 1),
            # Add more templates to reach 25 unique ones per section
        ],
        'Intelligence': [
            ("Find the missing number: 2, 5, 10, 17, ?", "24", "25", "26", "27", 2),
            ("If A=1, B=2, then CAT=?", "20", "24", "30", "22", 1),
            ("Giant : Dwarf :: Genius : ?", "Idiot", "Smart", "Tall", "Wise", 0),
            ("Odd one out: Rose, Lotus, Tulip, Apple", "Rose", "Lotus", "Tulip", "Apple", 3),
            ("If South-East becomes North, what will West become?", "South", "North-East", "South-East", "West", 2),
        ],
        'Quantitative': [
            ("If x + 1/x = 4, then x^2 + 1/x^2 = ?", "14", "16", "18", "12", 0),
            ("What is 15% of 200?", "20", "30", "40", "25", 1),
            ("Find the average of first 5 prime numbers", "5.6", "5.4", "5.8", "6.2", 2),
            ("A train travels 300km in 5 hours. Speed?", "50 km/h", "60 km/h", "70 km/h", "40 km/h", 1),
            ("Area of a circle with radius 7cm", "154 cm^2", "144 cm^2", "164 cm^2", "150 cm^2", 0),
        ],
        'Awareness': [
            ("Who was the first President of India?", "Nehru", "Rajendra Prasad", "Radhakrishnan", "Ambedkar", 1),
            ("Capital of France", "Berlin", "Madrid", "Paris", "Rome", 2),
            ("Currency of Japan", "Yuan", "Yen", "Won", "Dollar", 1),
            ("Highest mountain in the world", "K2", "Everest", "Kangchenjunga", "Lhotse", 1),
            ("Which planet is known as the Red Planet?", "Venus", "Mars", "Jupiter", "Saturn", 1),
        ]
    }

    # Expand the pool to 25 items each (auto-generating variations for now)
    sections = ['English', 'Intelligence', 'Quantitative', 'Awareness']
    
    for mt in [mt4, mt5]:
        if mt.questions.count() >= 100:
            print(f"{mt.name} already has questions. Skipping generation.")
            continue
            
        print(f"Populating {mt.name}...")
        q_count = 0
        for section in sections:
            templates = pool[section]
            for i in range(25):
                # Pick a template or create a variation
                template = templates[i % len(templates)]
                q_text = f"[{section}] Q{i+1}: {template[0]}"
                if i >= len(templates):
                    q_text += f" (Set {i // len(templates) + 1})"
                
                Question.objects.get_or_create(
                    test=mt,
                    question_text=q_text,
                    option1=template[1],
                    option2=template[2],
                    option3=template[3],
                    option4=template[4],
                    correct_option=template[5],
                    section=section,
                    order=q_count + 1
                )
                q_count += 1
        print(f"Added {q_count} questions to {mt.name}")

if __name__ == "__main__":
    add_mock_tests()
