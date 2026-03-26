from accounts.models import StudyTopic
import json

def populate_batch_2():
    data = [
        {
            "name": "Number Systems",
            "theory": "### Number Systems Overview\n"
                      "Number systems deal with classification and operations on numbers. "
                      "Key types include:\n"
                      "- **Natural Numbers**: 1, 2, 3...\n"
                      "- **Whole Numbers**: 0, 1, 2...\n"
                      "- **Integers**: ...-2, -1, 0, 1, 2...\n"
                      "- **Rational Numbers**: p/q form where q != 0.\n"
                      "- **Irrational Numbers**: Cannot be written as p/q (e.g., √2, π).\n"
                      "- **Prime Numbers**: Numbers having exactly two factors (1 and itself).",
            "formulas": "1. Sum of first 'n' natural numbers = [n(n+1)] / 2\n"
                        "2. Sum of squares of first 'n' natural numbers = [n(n+1)(2n+1)] / 6\n"
                        "3. Sum of cubes of first 'n' natural numbers = [[n(n+1)] / 2]²\n"
                        "4. **Divisibility Rules**:\n"
                        "   - 2: Last digit is even.\n"
                        "   - 3: Sum of digits divisible by 3.\n"
                        "   - 5: Last digit is 0 or 5.\n"
                        "   - 9: Sum of digits divisible by 9.",
            "examples": [
                {
                    "question": "Find the sum of the first 20 natural numbers.",
                    "solution": "**Step 1: Identify n.**\nn = 20.\n\n**Step 2: Apply the formula.**\nSum = [n(n+1)] / 2 = [20(20+1)] / 2\nSum = (20 × 21) / 2 = 10 × 21 = 210.\n\n**Final Result:** The sum is **210**."
                }
            ]
        },
        {
            "name": "Time and Work",
            "theory": "### Time and Work Concepts\n"
                      "Work is directly proportional to time and inversely proportional to efficiency.\n"
                      "- **Work done = Rate of Work (Efficiency) × Time Taken**\n"
                      "- If a person can do a piece of work in 'n' days, then work done per day = 1/n.\n"
                      "- Total Work is often assumed as 1 unit or the LCM of the time taken by different individuals.",
            "formulas": "1. If A takes 'x' days and B takes 'y' days, then together they take: **(xy) / (x + y)** days.\n"
                        "2. **Chain Rule**: (M1 × D1 × H1) / W1 = (M2 × D2 × H2) / W2\n"
                        "   (M = Men, D = Days, H = Hours, W = Work)",
            "examples": [
                {
                    "question": "A can do a piece of work in 10 days and B can do the same work in 15 days. How long will they take working together?",
                    "solution": "**Step 1: Find 1 day's work of each.**\nA's 1 day work = 1/10\nB's 1 day work = 1/15\n\n**Step 2: Find their combined 1 day's work.**\n(1/10 + 1/15) = (3 + 2) / 30 = 5 / 30 = 1/6.\n\n**Step 3: Calculate total time.**\nTime taken together = 1 / (1/6) = 6 days.\n\n**Final Result:** They will take **6 days**."
                }
            ]
        },
        {
            "name": "Direction Sense",
            "theory": "### The Four Cardinal Directions\n"
                      "There are four main directions: North (N), South (S), East (E), and West (W). "
                      "In addition, there are four sub-cardinal directions: North-East (NE), North-West (NW), South-East (SE), and South-West (SW).\n\n"
                      "- **Angle of Turn**: Right turn (Clockwise 90°) and Left turn (Anti-clockwise 90°).\n"
                      "- **Pythagoras Theorem**: Used for calculating the shortest (displacement) distance.\n"
                      "  (Hypotenuse)² = (Base)² + (Perpendicular)²",
            "formulas": "1. 1 degree CW = Right turn.\n"
                        "2. 1 degree ACW = Left turn.\n"
                        "3. Start position vs End position vector calculation.",
            "examples": [
                {
                    "question": "A man starts walking North. He turns right, walks some distance, then turns left. Which direction is he facing now?",
                    "solution": "**Step 1: Start Direction** -> North.\n**Step 2: Turn Right** -> Now facing East.\n**Step 3: Turn Left** -> From East, turning 90° anti-clockwise leads back to North.\n\n**Final Result:** He is facing **North**."
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
    populate_batch_2()
