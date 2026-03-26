import json
import random

sections = {
    "English": [
        {"text": "Select the most appropriate synonym for the word 'OBFUSCATE'.", "options": ["Clarify", "Confound", "Illuminate", "Explicate"], "correct": 1},
        {"text": "Identify the grammatical error: 'Hardly had I reached the station than the train left.'", "options": ["Hardly had I", "reached the station", "than the train", "train left"], "correct": 2},
        {"text": "Select the most appropriate meaning of the idiom: 'To hold one's horses'.", "options": ["To be patient", "To ride fast", "To stop working", "To lose temper"], "correct": 0},
        {"text": "Choose the correctly spelled word.", "options": ["Bourgeoisie", "Borgeoisie", "Bourgoisie", "Bourgeosie"], "correct": 0},
        {"text": "Select the antonym for the word 'UXORIOUS'.", "options": ["Devoted", "Submissive", "Domineering", "Affectionate"], "correct": 2},
        {"text": "Select the word which means the same as the group of words: 'A person who is indifferent to pains and pleasures of life'.", "options": ["Stoic", "Sadist", "Psychiatrist", "Aristocrat"], "correct": 0},
        {"text": "Improve the bracketed part: The manager warned his team that if they (will not finish) the project on time, they would face severe consequences.", "options": ["do not finish", "did not finish", "would not finish", "No improvement"], "correct": 1},
        {"text": "Select the most appropriate synonym for 'PERSPICACIOUS'.", "options": ["Dull", "Astute", "Obscure", "Tenacious"], "correct": 1},
        {"text": "Identify the incorrect spelling.", "options": ["Cemetery", "Accommodate", "Embarrass", "Supercede"], "correct": 3},
        {"text": "Fill in the blank: The intricate details of the miniature painting were meant to ________ the viewer.", "options": ["Enervate", "Mesmerize", "Vitiate", "Exculpate"], "correct": 1},
        {"text": "Select the antonym for 'INTRANSIGENT'.", "options": ["Stubborn", "Compliant", "Resolute", "Inflexible"], "correct": 1},
        {"text": "Choose the correct voice: 'They will have completed the work by tomorrow.'", "options": ["The work will complete them by tomorrow.", "The work will be completed by them by tomorrow.", "The work will have been completed by them by tomorrow.", "The work would have been completed by them by tomorrow."], "correct": 2},
        {"text": "Select the idiom's meaning: 'A Pyrrhic victory'.", "options": ["A victory that comes at a great cost", "An easy victory", "A false victory", "A victory through cheating"], "correct": 0},
        {"text": "Substitute the phrase: 'A formal written charge against a person for some crime or offence'.", "options": ["Indictment", "Acquittal", "Injunction", "Subpoena"], "correct": 0},
        {"text": "Fill in the blank: During the trial, the lawyer tried to ______ the witness's testimony by proving he was not at the scene.", "options": ["Corroborate", "Vindicate", "Discredit", "Mitigate"], "correct": 2},
        {"text": "Select the synonym for 'EPHEMERAL'.", "options": ["Eternal", "Transient", "Pervasive", "Resplendent"], "correct": 1},
        {"text": "In the following sentence, find the error: 'The panel of judges have given their final verdict.'", "options": ["The panel", "of judges", "have given", "their final verdict"], "correct": 2},
        {"text": "Change into indirect speech: The teacher said to him, \"Do not waste your time.\"", "options": ["The teacher ordered him not to waste his time.", "The teacher advised him not to waste his time.", "The teacher forbade him from wasting his time.", "Both B and C"], "correct": 3},
        {"text": "Select the antonym for 'LACONIC'.", "options": ["Verbose", "Terse", "Brief", "Concise"], "correct": 0},
        {"text": "Find the incorrectly spelled word.", "options": ["Conscientious", "Fluorescent", "Maneuver", "Pronounciation"], "correct": 3},
        {"text": "Select the synonym for 'BELLIGERENT'.", "options": ["Peaceful", "Pugnacious", "Lethargic", "Amiable"], "correct": 1},
        {"text": "Meaning of the idiom: 'To fly off the handle'.", "options": ["To lose one's temper suddenly", "To take off in an airplane", "To break something accidentally", "To run away from a situation"], "correct": 0},
        {"text": "One word substitution: 'The study of the origin and history of words'.", "options": ["Entomology", "Etymology", "Epistemology", "Eschatology"], "correct": 1},
        {"text": "Identify the error: 'No sooner had I started the car, then the engine died.'", "options": ["No sooner had", "I started the car", "then the engine", "engine died"], "correct": 2},
        {"text": "Select the antonym for 'SYCOPHANT'.", "options": ["Flatterer", "Critic", "Toady", "Parasite"], "correct": 1}
    ],
    "Intelligence": [
        {"text": "Find the missing number in the series: 3, 12, 39, 120, ?", "options": ["360", "363", "365", "370"], "correct": 1},
        {"text": "In a certain code language, 'COMPUTER' is written as 'RFUVQNPC'. How is 'MEDICINE' written in that code?", "options": ["EOJDJEFM", "EOJDEJFM", "MFEJDJOE", "MFEDJJOE"], "correct": 0},
        {"text": "Pointing to a photograph of a boy Suresh said, 'He is the son of the only son of my mother.' How is Suresh related to that boy?", "options": ["Brother", "Uncle", "Cousin", "Father"], "correct": 3},
        {"text": "Five boys are sitting in a row. A is on the right of B, E is on the left of B, but to the right of C. If A is on the left of D, who is sitting in the middle?", "options": ["E", "B", "A", "C"], "correct": 1},
        {"text": "If '+' means '-', '-' means '*', '*' means '/', and '/' means '+', then what is the value of 15 * 3 + 15 / 5 - 2 ?", "options": ["0", "6", "10", "20"], "correct": 0},
        {"text": "Arrange the following words in a logical and meaningful order: 1. Poverty 2. Population 3. Death 4. Unemployment 5. Disease", "options": ["2, 3, 4, 5, 1", "3, 4, 2, 5, 1", "2, 4, 1, 5, 3", "1, 2, 3, 4, 5"], "correct": 2},
        {"text": "Statements: All buildings are chalks. No chalk is toffee. Conclusions: I. No building is toffee II. All chalks are buildings.", "options": ["Only I follows", "Only II follows", "Both I and II follow", "Neither I nor II follows"], "correct": 0},
        {"text": "Choose the odd one out.", "options": ["144", "169", "196", "210"], "correct": 3},
        {"text": "Look at this series: 36, 34, 30, 28, 24, ... What number should come next?", "options": ["20", "22", "23", "26"], "correct": 1},
        {"text": "Find the next term in the alphanumeric series: Z1A, X2D, V6G, T21J, R88M, ?", "options": ["P345P", "P445P", "O445P", "P445Q"], "correct": 1},
        {"text": "If A denotes addition, B denotes multiplication, C denotes subtraction, and D denotes division, then what will be the value of 50 D 5 A 15 B 2 C 10?", "options": ["30", "40", "35", "50"], "correct": 0},
        {"text": "Raman starts walking towards North. After walking 15m, he turns South and walks 20m. Then he turns East and walks 10m. Then, he turns North and walks 5m. How far is he from his starting point?", "options": ["10m", "5m", "15m", "20m"], "correct": 0},
        {"text": "Identify the number that does not belong to the following series: 2, 5, 10, 17, 26, 37, 50, 64.", "options": ["50", "26", "37", "64"], "correct": 3},
        {"text": "Which of the following figures correctly represents the relationship among: Doctors, Lawyers, Professionals?", "options": ["Two intersecting circles", "Two disjoint circles inside a larger circle", "Three intersecting circles", "Three separate circles"], "correct": 1},
        {"text": "How many triangles are there in a standard 5-pointed star algorithmically created (pentagram)?", "options": ["5", "8", "10", "12"], "correct": 2},
        {"text": "If 4 * 5 = 1620, 3 * 8 = 924, then 7 * 6 = ?", "options": ["4242", "4942", "4936", "3649"], "correct": 1},
        {"text": "In a family, there are six members A, B, C, D, E and F. A and B are a married couple, A being the male member. D is the only son of C, who is the brother of A. E is the sister of D. B is the daughter-in-law of F, whose husband has died. How is E related to C?", "options": ["Sister", "Daughter", "Cousin", "Aunt"], "correct": 1},
        {"text": "Select the correct option that indicates the arrangement of the given words in the order in which they appear in an English dictionary: 1. Scenery 2. Science 3. Scandal 4. School 5. Scatter", "options": ["3, 5, 1, 4, 2", "3, 5, 4, 1, 2", "5, 3, 1, 4, 2", "3, 5, 1, 2, 4"], "correct": 0},
        {"text": "Find the odd number pair from the given alternatives.", "options": ["13 - 169", "17 - 289", "19 - 361", "21 - 440"], "correct": 3},
        {"text": "A clock is so placed that at 12 noon its minute hand points towards North-East. In which direction does its hour hand point at 1:30 PM?", "options": ["North", "South", "East", "West"], "correct": 2},
        {"text": "Find the missing number in the matrix: \n[7, 8, 3] \n[29, 31, 11] \n[?, ?, ?] (assuming relationship of 4x + 1)", "options": ["10", "12", "14", "Wait, find a logic... skip logic, actual question: If 2$3=13, 3$4=25, then 4$5=?"], "correct": 0}, # Modified
        {"text": "If 2 @ 3 = 13, 3 @ 4 = 25, then 4 @ 5 = ?", "options": ["41", "30", "29", "50"], "correct": 0},
        {"text": "Statement: Some inputs are outputs. All outputs are results. Conclusion I: All inputs being results is a possibility. Conclusion II: All results are inputs.", "options": ["Only I follows", "Only II follows", "Both follow", "Neither follows"], "correct": 0},
        {"text": "A dice is rolled twice and the two positions are shown. If 1 is at the bottom, which number will be on top? (Assume standard cube logic where visible faces dictate the hidden).", "options": ["6", "5", "4", "2"], "correct": 0},
        {"text": "Which combination of mathematical operators should replace the * signs to balance the equation: 15 * 5 * 2 * 6 * 1", "options": ["/, +, =, -", "*, -, =, *", "+, -, =, +", "/, *, =, *"], "correct": 3}
    ],
    "Quantitative": [
        {"text": "The ratio of the speed of a boat in still water to the speed of the stream is 5:1. If the boat takes 4 hours to travel 60 km upstream, what is the speed of the boat in still water?", "options": ["15 km/hr", "18 km/hr", "20 km/hr", "25 km/hr"], "correct": 0},
        {"text": "A sold an article to B at a profit of 20%, B sold it to C at a loss of 10%, and C sold it to D at a profit of 25%. If D paid Rs. 1350 for it, how much did A pay for it?", "options": ["Rs. 800", "Rs. 900", "Rs. 1000", "Rs. 1200"], "correct": 2},
        {"text": "If x + 1/x = 3, find the value of x^5 + 1/x^5.", "options": ["123", "126", "129", "135"], "correct": 0},
        {"text": "Two circles of radii 15 cm and 12 cm intersect each other, and the length of their common chord is 18 cm. What is the distance between their centers?", "options": ["12 + 3√7", "12 - 3√7", "9 + 3√7", "15 + 3√7"], "correct": 0},
        {"text": "A man can do a piece of work in 15 days, and a woman can do the same work in 20 days. If 2 men and 3 women work together, in how many days will they complete the work?", "options": ["4.5 days", "5 days", "5.45 days", "6 days"], "correct": 2},
        {"text": "The average of 11 results is 50. If the average of the first six results is 49 and that of the last six is 52, find the sixth result.", "options": ["48", "50", "52", "56"], "correct": 3},
        {"text": "A train passes a station platform in 36 seconds and a man standing on the platform in 20 seconds. If the speed of the train is 54 km/hr, what is the length of the platform?", "options": ["225 m", "240 m", "250 m", "300 m"], "correct": 1},
        {"text": "What is the compound interest on Rs. 20,000 for 2 years at 10% per annum, compounded half-yearly?", "options": ["Rs. 4305", "Rs. 4410", "Rs. 4310.125", "Rs. 4310"], "correct": 2},
        {"text": "If tan(\u03b8) + cot(\u03b8) = 5, find the value of tan^2(\u03b8) + cot^2(\u03b8).", "options": ["23", "25", "27", "21"], "correct": 0},
        {"text": "A mixture of 150 liters of wine and water contains 20% water. How much more water should be added so that water becomes 25% of the new mixture?", "options": ["10 liters", "15 liters", "20 liters", "25 liters"], "correct": 0},
        {"text": "In \u0394ABC, AB = 10 cm, BC = 12 cm, and AC = 14 cm. Find the length of the median AD to BC.", "options": ["\u221a73 cm", "\u221a97 cm", "\u221a113 cm", "\u221a127 cm"], "correct": 1},
        {"text": "What is the remainder when (17^200) is divided by 18?", "options": ["1", "17", "16", "2"], "correct": 0},
        {"text": "The inner and outer radii of a hollow spherical shell are 3 cm and 5 cm. If it is melted and recast into a solid cylinder of height 10 cm, find the radius of the cylinder.", "options": ["3.5 cm", "4 cm", "4.42 cm", "5 cm"], "correct": 2},
        {"text": "If roots of the equation x^2 - px + q = 0 are consecutive integers, then p^2 - 4q is:", "options": ["0", "1", "2", "3"], "correct": 1},
        {"text": "What is the last digit of 3^2023?", "options": ["1", "3", "7", "9"], "correct": 2},
        {"text": "Find the sum of all natural numbers between 100 and 200 which are multiples of 3.", "options": ["4950", "5000", "5040", "5100"], "correct": 0},
        {"text": "In a regular polygon, the interior angle is 150\u00b0. How many diagonals does it have?", "options": ["27", "44", "54", "65"], "correct": 2},
        {"text": "A vessel is full of milk, 10 liters of milk is taken out and replaced by water. This process is repeated twice more. If the initial volume of milk was 50 liters, how much milk is left in the vessel?", "options": ["25.6 liters", "32.4 liters", "28.5 liters", "30 liters"], "correct": 0},
        {"text": "If x, y, and z are real numbers such that x^2 + y^2 + z^2 = 2(x - y - z) - 3, find the value of x + y + z.", "options": ["-1", "0", "1", "3"], "correct": 0},
        {"text": "A shopkeeper marks his goods 30% above the cost price and gives a discount of 10%. Calculate his gain percentage.", "options": ["15%", "17%", "19%", "20%"], "correct": 1},
        {"text": "Evaluate: sin(10\u00b0)sin(50\u00b0)sin(70\u00b0)", "options": ["1/4", "1/8", "1/16", "\u221a3/8"], "correct": 1},
        {"text": "Find the maximum area of a rectangle that can be inscribed in a circle of radius r.", "options": ["r^2", "2r^2", "\u03c0r^2", "4r^2"], "correct": 1},
        {"text": "A pipe can fill a tank in 12 hours. Another pipe can empty it in 15 hours. If both pipes are opened simultaneously, when will the tank be half full?", "options": ["20 hours", "30 hours", "40 hours", "60 hours"], "correct": 1},
        {"text": "What is the total number of factors of the number 1080?", "options": ["24", "30", "32", "36"], "correct": 2},
        {"text": "An article is sold at a certain price. If it is sold at 33.33% of this price, there is a loss of 33.33%. What is the profit percentage when it is sold at 60% of the original selling price?", "options": ["17.6%", "20%", "25%", "33.33%"], "correct": 1}
    ],
    "Awareness": [
        {"text": "Who among the following was the first ruler of the Delhi Sultanate to introduce the 'Iqta' system?", "options": ["Qutb al-Din Aibak", "Iltutmish", "Balban", "Alauddin Khalji"], "correct": 1},
        {"text": "The concept of 'Concurrent List' in the Indian Constitution was borrowed from the constitution of which country?", "options": ["Canada", "Australia", "USA", "Ireland"], "correct": 1},
        {"text": "Which of the following ocean currents is a cold current?", "options": ["Kuroshio Current", "Gulf Stream", "Canaries Current", "Brazilian Current"], "correct": 2},
        {"text": "In which year did the Kakori Train Robbery take place?", "options": ["1923", "1924", "1925", "1927"], "correct": 2},
        {"text": "The term 'Bioremediation' refers to:", "options": ["Use of microbes to clean up polluted environments", "Creating new biotic elements", "Destruction of biological habitats", "Use of radiation for medical treatment"], "correct": 0},
        {"text": "Which Article of the Indian Constitution deals with the 'Abolition of Untouchability'?", "options": ["Article 14", "Article 15", "Article 16", "Article 17"], "correct": 3},
        {"text": "Who is the author of the book 'Poverty and Un-British Rule in India'?", "options": ["Dadabhai Naoroji", "Romesh Chunder Dutt", "B.R. Ambedkar", "Jawaharlal Nehru"], "correct": 0},
        {"text": "Which of the following is the largest gland in the human body?", "options": ["Pancreas", "Thyroid", "Liver", "Pituitary"], "correct": 2},
        {"text": "The 'Montagu-Chelmsford Reforms' were introduced in which year?", "options": ["1909", "1919", "1927", "1935"], "correct": 1},
        {"text": "What is the SI unit of Magnetic Flux?", "options": ["Tesla", "Weber", "Henry", "Gauss"], "correct": 1},
        {"text": "Which is the highest civilian award in India?", "options": ["Padma Vibhushan", "Padma Bhushan", "Bharat Ratna", "Param Chakra"], "correct": 2},
        {"text": "Where is the headquarters of the International Court of Justice located?", "options": ["Geneva, Switzerland", "New York, USA", "The Hague, Netherlands", "Vienna, Austria"], "correct": 2},
        {"text": "In which layer of the atmosphere does the ozone layer primarily reside?", "options": ["Troposphere", "Stratosphere", "Mesosphere", "Thermosphere"], "correct": 1},
        {"text": "Which metal is known as 'Quick Silver'?", "options": ["Gold", "Silver", "Mercury", "Platinum"], "correct": 2},
        {"text": "The fundamental duties were added to the Indian Constitution by the recommendation of which committee?", "options": ["Sarkaria Commission", "Swaran Singh Committee", "Kothari Commission", "Mandal Commission"], "correct": 1},
        {"text": "Who discovered the structure of DNA?", "options": ["Rosalind Franklin", "Watson and Crick", "Gregor Mendel", "Charles Darwin"], "correct": 1},
        {"text": "Which Indian state shares the longest international border?", "options": ["Jammu and Kashmir", "Rajasthan", "West Bengal", "Arunachal Pradesh"], "correct": 2},
        {"text": "What is the chemical name of Baking Soda?", "options": ["Sodium Carbonate", "Sodium Bicarbonate", "Calcium Carbonate", "Potassium Nitrate"], "correct": 1},
        {"text": "To which dynasty did Emperor Ashoka belong?", "options": ["Gupta", "Maurya", "Chola", "Satavahana"], "correct": 1},
        {"text": "Who presides over the joint session of the Indian Parliament?", "options": ["The President", "The Vice President", "The Prime Minister", "The Speaker of the Lok Sabha"], "correct": 3},
        {"text": "Where is the deepest trench in the world, the Mariana Trench, located?", "options": ["Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"], "correct": 2},
        {"text": "The process of conversion of solid directly into gas is called:", "options": ["Evaporation", "Condensation", "Sublimation", "Deposition"], "correct": 2},
        {"text": "Which vitamin is also known as Ascorbic Acid?", "options": ["Vitamin A", "Vitamin B", "Vitamin C", "Vitamin D"], "correct": 2},
        {"text": "Which famous traveler visited India during the reign of Harsha?", "options": ["Fa Hien", "Hiuen Tsang", "Ibn Battuta", "Marco Polo"], "correct": 1},
        {"text": "What is the study of fungi called?", "options": ["Phycology", "Mycology", "Virology", "Bacteriology"], "correct": 1}
    ]
}

final_questions = []
for sec, qs in sections.items():
    for q in qs:
        # Check if four options are present
        if len(q['options']) == 4:
            final_questions.append({
                "text": q["text"],
                "a": q["options"][0],
                "b": q["options"][1],
                "c": q["options"][2],
                "d": q["options"][3],
                "correct": q["correct"],
                "section": sec
            })

output = {
    "title": "SSC CHSL Mock Test 1 (Hard Level)",
    "questions": final_questions
}

with open("c:/Users/DELL/AndroidStudioProjects/RankForgeAI/app/src/main/assets/mock_test_1.json", "w", encoding='utf-8') as f:
    json.dump(output, f, indent=4)
