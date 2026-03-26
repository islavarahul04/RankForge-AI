from accounts.models import StudyTopic
import json

def update_more():
    data = [
        {
            "name": "Averages",
            "theory": "### Averages / Mean\n"
                      "An average is a single value that represents a set of values. "
                      "In mathematics, it is often refers to the 'Arithmetic Mean'.\n\n"
                      "**Concept**:\n"
                      "- The sum of all values divided by the number of values.\n"
                      "- If the average age of 5 students is 20, then the total sum of their ages is 100.",
            "formulas": "1. **Average = (Sum of Observations) / (Number of Observations)**\n"
                        "2. **Sum = Average × Number of Observations**\n"
                        "3. **Average Speed** (distance constant): 2xy / (x+y)\n"
                        "4. **Weighted Average**: (n1x1 + n2x2) / (n1 + n2)",
            "examples": [
                {
                    "question": "The average weight of 8 persons is increased by 2.5 kg when a new person comes in place of one of them weighing 65 kg. What is the weight of the new person?",
                    "solution": "**Step 1: Identify the total increase.**\nTotal increase = Number of persons × Increase per person\nTotal increase = 8 × 2.5 = 20 kg.\n\n**Step 2: Calculate the weight of the new person.**\nSince the average increased, the new person must be heavier than the one who left.\nWeight of new person = Weight of person who left + Total increase\nWeight of new person = 65 + 20 = 85 kg.\n\n**Final Result:** The new person weighs **85 kg**."
                }
            ]
        },
        {
            "name": "Mensuration",
            "theory": "### Mensuration\n"
                      "Mensuration is the branch of mathematics that deals with the measurement of geometric figures and their parameters like length, area, and volume.\n\n"
                      "- **2D Shapes**: Area (Square units) and Perimeter (Linear units).\n"
                      "- **3D Shapes**: Surface Area and Volume (Cubic units).",
            "formulas": "1. **Square**: Area = a², Perimeter = 4a.\n"
                        "2. **Rectangle**: Area = L × B, Perimeter = 2(L + B).\n"
                        "3. **Circle**: Area = πr², Circumference = 2πr.\n"
                        "4. **Cube**: Volume = a³, Surface Area = 6a².\n"
                        "5. **Cylinder**: Volume = πr²h, CSA = 2πrh.",
            "examples": [
                {
                    "question": "Find the area of a circular field whose radius is 7 meters. (Use π = 22/7)",
                    "solution": "**Step 1: Identify given values.**\nRadius (r) = 7 m.\n\n**Step 2: Apply the formula.**\nArea = π × r²\nArea = (22/7) × 7 × 7\n\n**Step 3: Calculate.**\nArea = 22 × 7 = 154 sq. meters.\n\n**Final Result:** The area is **154 sq. meters**."
                }
            ]
        },
        {
            "name": "Venn Diagrams",
            "theory": "### Venn Diagrams\n"
                      "A Venn diagram illustrates the relationships between different groups or 'sets' of things. "
                      "Usually represented by overlapping circles.\n\n"
                      "- **Intersection**: The overlapping part, representing what is common between groups.\n"
                      "- **Union**: The combined area of all circles, representing everything in any of the groups.",
            "formulas": "1. n(A ∪ B) = n(A) + n(B) - n(A ∩ B)\n"
                        "2. For logical puzzles: Identify the most restricted group (intersection) first and work outwards.",
            "examples": [
                {
                    "question": "In a class of 50 students, 30 like Cricket, 25 like Football, and 10 like both. How many students like neither?",
                    "solution": "**Step 1: Use the Union formula.**\nTotal students who like either = (Like Cricket) + (Like Football) - (Like Both)\nTotal = 30 + 25 - 10 = 45.\n\n**Step 2: Subtract from class total.**\nStudents who like neither = Total students - Total who like either\nNeither = 50 - 45 = 5.\n\n**Final Result:** **5** students like neither."
                }
            ]
        }
    ]

    for item in data:
        topic = StudyTopic.objects.filter(name__icontains=item["name"]).first()
        if topic:
            topic.theory = item["theory"]
            topic.formulas = item["formulas"]
            topic.examples = item["examples"]
            topic.save()
            print(f"Updated: {topic.name}")

if __name__ == "__main__":
    update_more()
