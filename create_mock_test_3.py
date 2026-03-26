import os
import django
import sys

# Set up Django environment
sys.path.append(os.path.join(os.getcwd(), 'backend'))
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from accounts.models import MockTest, Question

def create_mock_test_3():
    # 1. Create Mock Test object
    test, created = MockTest.objects.get_or_create(
        name="SSC CHSL Mock Test 3 (Ultimate Difficulty)",
        defaults={'is_free': False, 'order': 3}
    )
    
    if not created:
        print(f"Test '{test.name}' already exists. Updating questions...")
        test.questions.all().delete()
    else:
        print(f"Created new test: {test.name}")

    questions_data = []

    # --- Quantitative Aptitude (25) ---
    q_quant = [
        ("The sum of the digits of a 3 digit number is subtracted from the number. The resulting number is always divisible by?", "9", "6", "3", "All of these", 3, "Quantitative"),
        ("If x + 1/x = sqrt(3), then x^18 + x^12 + x^6 + 1 is?", "0", "1", "2", "3", 0, "Quantitative"),
        ("In what ratio must water be mixed with milk to gain 20% by selling the mixture at cost price?", "1:5", "5:1", "1:6", "6:1", 0, "Quantitative"),
        ("A can do a work in 15 days and B in 20 days. If they work on it together for 4 days, then the fraction of the work that is left is?", "1/4", "1/10", "7/15", "8/15", 3, "Quantitative"),
        ("Find the value of (sec θ + tan θ)(1 - sin θ).", "cos θ", "sin θ", "tan θ", "sec θ", 0, "Quantitative"),
        ("The ratio of the speed of a car, a train and a bus is 5 : 9 : 4. The average speed of the car, the bus and the train is 72 km/hr. What is the average speed of the car and the train together?", "82 km/hr", "84 km/hr", "78 km/hr", "86 km/hr", 1, "Quantitative"),
        ("The largest natural number which exactly divides the product of any four consecutive natural numbers is?", "12", "24", "48", "120", 1, "Quantitative"),
        ("The remainder when 2^31 is divided by 5 is?", "1", "2", "3", "4", 2, "Quantitative"),
        ("If a^3 + b^3 = 5824 and a + b = 28, then (a - b)^2 + ab is equal to?", "208", "152", "180", "164", 0, "Quantitative"),
        ("Find the value of cot 10° cot 20° cot 60° cot 70° cot 80°.", "1/√3", "√3", "1", "0", 0, "Quantitative"),
        ("A merchant marked the price of an article by 40% above the cost price and gave a discount of 25% on it. What is his profit percentage?", "5%", "10%", "15%", "20%", 0, "Quantitative"),
        ("What is the distance between the points (3, 4) and (-2, -1)?", "5√2", "2√5", "6", "4√3", 0, "Quantitative"),
        ("The arithmetic mean of 11 observations is 50. If the arithmetic mean of the first six observations is 49 and that of the last six is 52, then the sixth observation is?", "52", "54", "56", "58", 2, "Quantitative"),
        ("A sphere of radius 6 cm is melted and recast into a cone of radius 3 cm. Find the height of the cone.", "96 cm", "48 cm", "24 cm", "72 cm", 0, "Quantitative"),
        ("How many diagonals are there in a decagon?", "35", "45", "55", "25", 0, "Quantitative"),
        ("The compound interest on Rs. 10,000 for 2 years at 10% per annum, compounded annually, is?", "Rs. 2,000", "Rs. 2,100", "Rs. 2,200", "Rs. 2,400", 1, "Quantitative"),
        ("If the area of an equilateral triangle is 16√3 cm^2, then its perimeter is?", "24 cm", "12 cm", "18 cm", "48 cm", 0, "Quantitative"),
        ("A box contains 5 red, 4 blue and 3 green balls. If three balls are drawn at random, what is the probability that none of them is red?", "7/44", "5/44", "9/44", "11/44", 0, "Quantitative"),
        ("The sum of the squares of three consecutive odd numbers is 251. The numbers are?", "7, 9, 11", "9, 11, 13", "5, 7, 9", "11, 13, 15", 0, "Quantitative"),
        ("Find the length of the longest pole that can be placed in a room 12 m long, 9 m broad and 8 m high.", "17 m", "15 m", "19 m", "21 m", 0, "Quantitative"),
        ("The HCF and LCM of two numbers are 12 and 72 respectively. If one of the numbers is 24, find the other common number.", "36", "48", "60", "72", 0, "Quantitative"),
        ("If log₁₀ x = -2, then x is?", "0.01", "0.1", "0.001", "100", 0, "Quantitative"),
        ("The slope of the line passing through (2, 3) and (4, 7) is?", "2", "3", "4", "1", 0, "Quantitative"),
        ("If tan A = 3/4, then sin A cos A is?", "12/25", "7/25", "14/25", "9/25", 0, "Quantitative"),
        ("A man rows a boat 18 km in 3 hours downstream and 12 km in 3 hours upstream. Find the speed of the man in still water.", "5 km/hr", "2 km/hr", "3 km/hr", "4 km/hr", 0, "Quantitative")
    ]

    # --- General Intelligence & Reasoning (25) ---
    q_reasoning = [
        ("Pointing to a lady, a man said, 'The son of her only brother is the brother of my wife'. How is the lady related to the man?", "Sister of father-in-law", "Mother-in-law", "Grandmother", "Sister", 0, "Intelligence"),
        ("If 'WATER' is coded as 'XBUFS', how is 'FIRE' coded?", "GJSF", "GKSH", "GJSF", "HKTG", 0, "Intelligence"),
        ("Statements: All pens are pencils. No pencil is an eraser. Conclusions: I. No eraser is a pen. II. No eraser is a pencil.", "Both I and II follow", "Only I follows", "Only II follows", "Neither follows", 0, "Intelligence"),
        ("Missing number: 2, 6, 12, 20, 30, ?", "42", "40", "44", "48", 0, "Intelligence"),
        ("If '+' means '×', '×' means '-', '-' means '÷' and '÷' means '+', then 16 + 4 ÷ 10 - 5 = ?", "66", "70", "74", "80", 0, "Intelligence"),
        ("Find the odd one out: 27, 64, 125, 144", "144", "27", "64", "125", 0, "Intelligence"),
        ("Arrange in logical order: 1. Death 2. Marriage 3. Education 4. Birth 5. Job", "4, 3, 5, 2, 1", "4, 5, 3, 2, 1", "3, 4, 5, 2, 1", "4, 3, 2, 5, 1", 0, "Intelligence"),
        ("In a row of 40 students, R is 11th from the right. What is his position from the left?", "30th", "29th", "31st", "32nd", 0, "Intelligence"),
        ("If P is the brother of Q, R is the father of P, S is the mother of T and T is the sister of Q. How is R related to S?", "Husband", "Wife", "Brother", "Father", 0, "Intelligence"),
        ("Select the related word: Clock : Time :: Thermometer : ?", "Temperature", "Heat", "Radiation", "Energy", 0, "Intelligence"),
        ("Which of the following does not belong to the group? Lion, Tiger, Leopard, Bear", "Bear", "Lion", "Tiger", "Leopard", 0, "Intelligence"),
        ("If A=1, B=2, then BID = ?", "15", "14", "16", "17", 0, "Intelligence"),
        ("How many squares are there in a 3x3 grid?", "14", "9", "10", "13", 0, "Intelligence"),
        ("Find the next term: Z, W, T, Q, ?", "N", "M", "L", "O", 0, "Intelligence"),
        ("If the day before yesterday was Thursday, when will Sunday be?", "Tomorrow", "Today", "Day after tomorrow", "Yesterday", 0, "Intelligence"),
        ("A man walks 5 km East, then turns right and walks 4 km, then turns left and walks 5 km. Which direction is he facing now?", "East", "West", "North", "South", 0, "Intelligence"),
        ("Statements: Some kings are queens. All queens are beautiful. Conclusions: I. All kings are beautiful. II. Some kings are beautiful.", "Only II follows", "Only I follows", "Both follow", "None follow", 0, "Intelligence"),
        ("Identify the correct alternative: 121 : 12 :: 169 : ?", "14", "13", "15", "16", 0, "Intelligence"),
        ("Find the odd letters: BD, HK, NP, TW", "HK", "BD", "NP", "TW", 0, "Intelligence"),
        ("If 1st Jan 2023 was Sunday, what was 31st Dec 2023?", "Sunday", "Monday", "Saturday", "Friday", 0, "Intelligence"),
        ("If FRIEND is coded as HUMJTK, how is CANDLE coded?", "EDRIRL", "DCQHQK", "ESJFME", "FYOBOC", 0, "Intelligence"),
        ("Which number is wrong in the series: 3, 5, 11, 14, 17, 21", "14", "11", "17", "21", 0, "Intelligence"),
        ("How many triangles are there in a star shape?", "10", "8", "6", "12", 0, "Intelligence"),
        ("Missing term: ACE, GIK, MOQ, ?", "SUW", "RTV", "STU", "TUV", 0, "Intelligence"),
        ("If RED is 27, then BLUE is?", "40", "36", "44", "48", 0, "Intelligence")
    ]

    # --- English Language (25) ---
    q_english = [
        ("Choose the synonym of 'ABERRANT'.", "Abnormal", "Common", "Normal", "Usual", 0, "English"),
        ("Identify the correctly spelled word.", "Entrepreneur", "Entreprenuer", "Enterpreneur", "Entreprenur", 0, "English"),
        ("Fill in the blank: The police _____ looking for the thief.", "are", "is", "was", "has", 0, "English"),
        ("Choose the antonym of 'BENEVOLENT'.", "Malevolent", "Kind", "Generous", "Friendly", 0, "English"),
        ("What is the meaning of the idiom 'To pay through the nose'?", "To pay an extremely high price", "To pay slowly", "To pay in installments", "To pay via bank transfer", 0, "English"),
        ("A person who hates mankind is called?", "Misanthrope", "Philanthropist", "Misogynist", "Polyglot", 0, "English"),
        ("Identify the part of speech of the underlined word: She ran **quickly**.", "Adverb", "Adjective", "Noun", "Verb", 0, "English"),
        ("Choose the correct passive voice: 'He is playing cricket.'", "Cricket is being played by him.", "Cricket was being played by him.", "Cricket is played by him.", "Cricket has been played by him.", 0, "English"),
        ("What is the synonym of 'CANDID'?", "Frank", "Deceptive", "Secretive", "Shy", 0, "English"),
        ("Select the correct indirect speech: He said, 'I am busy.'", "He said that he was busy.", "He said that he is busy.", "He says that he was busy.", "He said he is busy.", 0, "English"),
        ("Change to plural: 'Criterion'", "Criteria", "Criterions", "Criterias", "Criteriones", 0, "English"),
        ("Choose the correct preposition: He is good _____ English.", "at", "in", "with", "for", 0, "English"),
        ("Synonym of 'EPIPHANY'?", "Sudden realization", "Confusion", "Disaster", "Success", 0, "English"),
        ("Pick the antonym of 'FRUGAL'.", "Extravagant", "Miserly", "Thrifty", "Economical", 0, "English"),
        ("Identify the error: 'Each of the students have done their homework.'", "have", "Each", "students", "done", 0, "English"),
        ("One word substitution: A doctor who treats skin diseases.", "Dermatologist", "Cardiologist", "Neurologist", "Pediatrician", 0, "English"),
        ("Meaning of 'To break the ice'?", "To start a conversation", "To end a fight", "To feel cold", "To make someone angry", 0, "English"),
        ("Synonym of 'GREGARIOUS'?", "Sociable", "Quiet", "Sad", "Angry", 0, "English"),
        ("Antonym of 'Lethargic'?", "Energetic", "Lazy", "Slow", "Dull", 0, "English"),
        ("Correct sentence: 'If I was you, I would go.'", "If I were you, I would go.", "If I am you, I would go.", "If I had been you, I would go.", "No correction", 0, "English"),
        ("Meaning of 'Zenith'?", "Peak", "Bottom", "Middle", "Side", 0, "English"),
        ("Subtle meaning of 'Altruist'?", "One who puts others first", "One who loves art", "One who hates technology", "One who travels a lot", 0, "English"),
        ("Meaning of 'Egalitarian'?", "Believing in equality", "Believing in monarchy", "Believing in chaos", "Believing in wealth", 0, "English"),
        ("Choose the correct article: He is _____ honest man.", "an", "a", "the", "No article", 0, "English"),
        ("Identify the synonym of 'Pragmatic'.", "Practical", "Idealistic", "Theoretical", "Emotional", 0, "English")
    ]

    # --- General Awareness (25) ---
    q_awareness = [
        ("Which of the following Article of the Indian Constitution deals with 'Right to Freedom of Religion'?", "Articles 25-28", "Articles 14-18", "Articles 19-22", "Articles 23-24", 0, "Awareness"),
        ("Who was the first woman Governor of an Indian State?", "Sarojini Naidu", "Sucheta Kripalani", "Indira Gandhi", "Vijayalakshmi Pandit", 0, "Awareness"),
        ("The 'Quit India Movement' was launched in which year?", "1942", "1940", "1945", "1947", 0, "Awareness"),
        ("Which planet is known as the 'Red Planet'?", "Mars", "Venus", "Jupiter", "Saturn", 0, "Awareness"),
        ("The 'International Yoga Day' is celebrated on?", "June 21", "June 5", "April 22", "July 1", 0, "Awareness"),
        ("Who is the author of the book 'The God of Small Things'?", "Arundhati Roy", "Vikram Seth", "Salman Rushdie", "Anita Desai", 0, "Awareness"),
        ("In which year did the First World War begin?", "1914", "1912", "1918", "1939", 0, "Awareness"),
        ("The headquarters of the United Nations is located in?", "New York", "Geneva", "Paris", "London", 0, "Awareness"),
        ("Which river is known as the 'Ganga of the South'?", "Godavari", "Krishna", "Cauvery", "Mahanadi", 0, "Awareness"),
        ("Who was the first Indian to win a Nobel Prize?", "Rabindranath Tagore", "C.V. Raman", "Mother Teresa", "Amartya Sen", 0, "Awareness"),
        ("What is the capital of Australia?", "Canberra", "Sydney", "Melbourne", "Perth", 0, "Awareness"),
        ("The deficiency of which vitamin causes 'Rickets'?", "Vitamin D", "Vitamin A", "Vitamin B12", "Vitamin C", 0, "Awareness"),
        ("Which gas is most abundant in the Earth's atmosphere?", "Nitrogen", "Oxygen", "Carbon dioxide", "Argon", 0, "Awareness"),
        ("Who is known as the 'Iron Man of India'?", "Sardar Vallabhbhai Patel", "Mahatma Gandhi", "Jawaharlal Nehru", "Subhas Chandra Bose", 0, "Awareness"),
        ("The 'Battle of Plassey' was fought in which year?", "1757", "1764", "1857", "1526", 0, "Awareness"),
        ("Which state in India has the longest coastline?", "Gujarat", "Andhra Pradesh", "Tamil Nadu", "Maharashtra", 0, "Awareness"),
        ("The Great Barrier Reef is located in which country?", "Australia", "USA", "Brazil", "India", 0, "Awareness"),
        ("Who designed the Indian National Flag?", "Pingali Venkayya", "Rabindranath Tagore", "Mahatma Gandhi", "B.R. Ambedkar", 0, "Awareness"),
        ("Which element is used in the filament of an electric bulb?", "Tungsten", "Iron", "Copper", "Silver", 0, "Awareness"),
        ("The 'Simon Commission' visited India in which year?", "1928", "1919", "1930", "1942", 0, "Awareness"),
        ("What is the currency of Japan?", "Yen", "Yuan", "Won", "Baht", 0, "Awareness"),
        ("Which is the largest organ in the human body?", "Skin", "Liver", "Heart", "Lungs", 0, "Awareness"),
        ("The 'Preamble' of the Indian Constitution was amended by which act?", "42nd Amendment", "44th Amendment", "1st Amendment", "73rd Amendment", 0, "Awareness"),
        ("Who was the last Mughal Emperor of India?", "Bahadur Shah Zafar", "Aurangzeb", "Shah Jahan", "Akbar", 0, "Awareness"),
        ("Which country won the FIFA World Cup 2022?", "Argentina", "France", "Brazil", "Croatia", 0, "Awareness")
    ]

    all_questions = q_quant + q_reasoning + q_english + q_awareness
    
    for i, q_tuple in enumerate(all_questions):
        Question.objects.create(
            test=test,
            question_text=q_tuple[0],
            option1=q_tuple[1],
            option2=q_tuple[2],
            option3=q_tuple[3],
            option4=q_tuple[4],
            correct_option=q_tuple[5],
            section=q_tuple[6],
            order=i + 1
        )
    
    print(f"Successfully added 100 questions to '{test.name}'")

if __name__ == "__main__":
    create_mock_test_3()
