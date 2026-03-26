from accounts.models import StudyTopic
import json

def update_quant_gi():
    data = [
        {
            "name": "Ratio and Proportion",
            "theory": "### Ratio and Proportion\n"
                      "**Ratio**: A ratio is a comparison of two quantities of the same kind by division. "
                      "If a and b are two quantities, the ratio of a to b is a:b (or a/b).\n\n"
                      "**Proportion**: An equality of two ratios is called a proportion. "
                      "If a:b = c:d, then a, b, c, d are in proportion. Here, 'a' and 'd' are called extremes, "
                      "while 'b' and 'c' are called means.\n\n"
                      "**Key Properties**:\n"
                      "1. Product of Extremes = Product of Means (ad = bc).\n"
                      "2. Mean Proportional between a and b is √(ab).",
            "formulas": "1. **Compounded Ratio** of (a:b) and (c:d) is (ac:bd).\n"
                        "2. **Duplicate Ratio** of (a:b) is (a²:b²).\n"
                        "3. **Inverse Ratio** of (a:b) is (b:a).\n"
                        "4. **Componendo and Dividendo**: If a/b = c/d, then (a+b)/(a-b) = (c+d)/(c-d).",
            "examples": [
                {
                    "question": "If A:B = 2:3 and B:C = 4:5, find A:B:C.",
                    "solution": "**Step 1: Make the common term (B) equal.**\nIn A:B, B is 3. In B:C, B is 4.\nLCM of 3 and 4 is 12.\n\n**Step 2: Adjust both ratios.**\nA:B = (2×4) : (3×4) = 8:12\nB:C = (4×3) : (5×3) = 12:15\n\n**Step 3: Combine the ratios.**\nSince B is now 12 in both, A:B:C = 8:12:15.\n\n**Final Result:** A:B:C is **8:12:15**."
                }
            ]
        },
        {
            "name": "Ratio and Time",
            "theory": "### Ratio and Time\n"
                      "Ratio and Time concepts are often applied in problems involving speed, work, or sequences where time intervals "
                      "are compared using ratios. It is closely related to 'Time and Distance' and 'Time and Work'.\n\n"
                      "**Core Inverse Relationship**:\n"
                      "- If Speed is in ratio a:b, then Time taken for the same distance is in ratio **b:a**.\n"
                      "- If Efficiency of two workers is a:b, then Time taken for the same work is **b:a**.",
            "formulas": "1. Speed ∝ 1/Time (when Distance is constant).\n"
                        "2. Efficiency ∝ 1/Time (when Work is constant).\n"
                        "3. Ratio of Time = (1/Rate1) : (1/Rate2) : (1/Rate3)",
            "examples": [
                {
                    "question": "The ratio of speeds of two cars is 3:4. If the first car takes 20 minutes to cover a distance, how much time will the second car take?",
                    "solution": "**Step 1: Identify the ratio relationship.**\nRatio of Speeds = 3:4.\nSince Time is inversely proportional to Speed, Ratio of Time = 4:3.\n\n**Step 2: Set up the equation.**\nLet the time taken be 4x and 3x.\nGiven 4x = 20 minutes.\n\n**Step 3: Calculate x.**\nx = 20 / 4 = 5 minutes.\n\n**Step 4: Find second car's time.**\nTime for second car = 3x = 3 × 5 = 15 minutes.\n\n**Final Result:** The second car will take **15 minutes**."
                }
            ]
        },
        {
            "name": "Time and Distance",
            "theory": "### Time, Speed and Distance\n"
                      "This topic explores the relationship between how fast an object moves, the time it takes, and the distance covered.\n\n"
                      "**Basic Definitions**:\n"
                      "- **Speed**: Distance covered per unit time.\n"
                      "- **Average Speed**: Total distance covered divided by total time taken.\n"
                      "- **Relative Speed**: Speed of one object with respect to another.",
            "formulas": "1. **Distance = Speed × Time**\n"
                        "2. **km/hr to m/s conversion**: Multiply by 5/18.\n"
                        "3. **m/s to km/hr conversion**: Multiply by 18/5.\n"
                        "4. **Average Speed** (for same distance at speeds x and y): (2xy) / (x + y).\n"
                        "5. **Relative Speed**:\n"
                        "   - Same direction: (x - y)\n"
                        "   - Opposite direction: (x + y)",
            "examples": [
                {
                    "question": "A train travels at 72 km/hr. How much distance will it cover in 15 seconds?",
                    "solution": "**Step 1: Convert speed to m/s.**\nSpeed = 72 × (5/18) = 4 × 5 = 20 m/s.\n\n**Step 2: Identify time.**\nTime = 15 seconds.\n\n**Step 3: Calculate distance.**\nDistance = Speed × Time\nDistance = 20 m/s × 15 s = 300 meters.\n\n**Final Result:** The distance covered is **300 meters**."
                }
            ]
        },
        {
            "name": "Interest",
            "theory": "### Simple and Compound Interest\n"
                      "Interest is the price paid for using someone else's money.\n\n"
                      "- **Simple Interest (SI)**: calculated only on the principal amount.\n"
                      "- **Compound Interest (CI)**: calculated on the principal and also on the accumulated interest of previous periods.",
            "formulas": "1. **SI = (P × R × T) / 100**\n"
                        "2. **Amount (SI) = P + SI**\n"
                        "3. **Amount (CI) = P [1 + R/100]ᵀ**\n"
                        "4. **CI = Amount - P**\n"
                        "   (P=Principal, R=Rate, T=Time)",
            "examples": [
                {
                    "question": "Find the compound interest on ₹10,000 for 2 years at 10% per annum.",
                    "solution": "**Step 1: Identify P, R, T.**\nP = 10,000, R = 10, T = 2.\n\n**Step 2: Calculate Amount.**\nAmount = 10,000 × (1 + 10/100)²\nAmount = 10,000 × (1.1)² = 10,000 × 1.21 = 12,100.\n\n**Step 3: Calculate Interest.**\nCI = Amount - P = 12,100 - 10,000 = ₹2,100.\n\n**Final Result:** The interest is **₹2,100**."
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
    update_quant_gi()
