import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from accounts.models import StudyTopic, StudySubject
import json

def populate_concepts():
    # Expand and deepen concepts for various subjects
    data = [
        # --- Quantitative Aptitude ---
        {
            "name": "Geometry - Triangles",
            "theory": "### Geometry: Triangles\n"
                      "A triangle is a polygon with three edges and three vertices. It is one of the basic shapes in geometry.\n\n"
                      "**Types of Triangles**:\n"
                      "1. **By Sides**: Equilateral (all sides equal), Isosceles (two sides equal), Scalene (no sides equal).\n"
                      "2. **By Angles**: Acute (all angles < 90°), Right (one angle = 90°), Obtuse (one angle > 90°).\n\n"
                      "**Key Theorems**:\n"
                      "- **Angle Sum Property**: The sum of angles in a triangle is always 180°.\n"
                      "- **Exterior Angle Theorem**: Exterior angle is equal to the sum of two opposite interior angles.\n"
                      "- **Pythagoras Theorem**: In a right-angled triangle, a² + b² = c².",
            "formulas": "1. **Area** = (1/2) × Base × Height\n"
                        "2. **Heron's Formula**: Area = √[s(s-a)(s-b)(s-c)], where s = (a+b+c)/2\n"
                        "3. **In-radius of Right Triangle**: (P + B - H) / 2\n"
                        "4. **Circum-radius of Right Triangle**: Hypotenuse / 2",
            "examples": [
                {
                    "question": "The sides of a triangle are 13, 14, and 15 cm. Find its area.",
                    "solution": "**Step 1: Calculate semi-perimeter (s).**\ns = (13+14+15)/2 = 42/2 = 21.\n\n**Step 2: Apply Heron's Formula.**\nArea = √[21(21-13)(21-14)(21-15)]\nArea = √[21 × 8 × 7 × 6] = √[7056] = 84 sq cm.\n\n**Final Result:** Area is **84 sq cm**."
                }
            ]
        },
        {
            "name": "Mensuration - 3D Shapes",
            "theory": "### Mensuration: 3D Shapes\n"
                      "3D shapes occupy volume and have surface area. Common shapes include Cylinders, Cones, and Spheres.\n\n"
                      "**Cylinder**: A solid with two parallel circular bases.\n"
                      "**Cone**: A solid that tapers smoothly from a flat base to a point called the apex.\n"
                      "**Sphere**: A perfectly round geometrical object in three-dimensional space.",
            "formulas": "1. **Cylinder**: Volume = πr²h; CSA = 2πrh; TSA = 2πr(r+h)\n"
                        "2. **Cone**: Volume = (1/3)πr²h; CSA = πrl (where l=√(r²+h²)); TSA = πr(r+l)\n"
                        "3. **Sphere**: Volume = (4/3)πr³; Surface Area = 4πr²",
            "examples": [
                {
                    "question": "If the radius of a sphere is doubled, how many times does its volume increase?",
                    "solution": "**Step 1: Write original volume formula.**\nV1 = (4/3)πr³.\n\n**Step 2: Calculate new volume with radius 2r.**\nV2 = (4/3)π(2r)³ = (4/3)π(8r³) = 8 × [(4/3)πr³].\n\n**Step 3: Compare.**\nV2 = 8 × V1.\n\n**Final Result:** The volume becomes **8 times** the original."
                }
            ]
        },
        {
            "name": "Trigonometry - Identities",
            "theory": "### Trigonometric Identities\n"
                      "Identities are equations involving trigonometric functions that are true for all values of the variables.\n\n"
                      "**Fundamental Identities**:\n"
                      "- sin²θ + cos²θ = 1\n"
                      "- 1 + tan²θ = sec²θ\n"
                      "- 1 + cot²θ = cosec²θ\n\n"
                      "**Complementary Angles**:\n"
                      "- sin(90-θ) = cosθ\n"
                      "- tan(90-θ) = cotθ\n"
                      "- sec(90-θ) = cosecθ",
            "formulas": "1. **sin(A+B)** = sinA cosB + cosA sinB\n"
                        "2. **cos(A+B)** = cosA cosB - sinA sinB\n"
                        "3. **tan(A+B)** = (tanA + tanB) / (1 - tanA tanB)\n"
                        "4. **sin 2θ** = 2sinθ cosθ",
            "examples": [
                {
                    "question": "Find the value of (sin 30° + cos 60°) / tan 45°.",
                    "solution": "**Step 1: Substitute values.**\nsin 30° = 1/2, cos 60° = 1/2, tan 45° = 1.\n\n**Step 2: Calculate.**\nResult = (1/2 + 1/2) / 1 = 1 / 1 = 1.\n\n**Final Result:** The value is **1**."
                }
            ]
        },
        # --- English Language ---
        {
            "name": "Active and Passive Voice",
            "theory": "### Active and Passive Voice\n"
                      "**Active Voice**: The subject of the sentence performs the action (e.g., 'The cat chased the mouse').\n"
                      "**Passive Voice**: The subject receives the action (e.g., 'The mouse was chased by the cat').\n\n"
                      "**Rules for Conversion**:\n"
                      "1. Object of active becomes subject of passive.\n"
                      "2. Use 'to be' verb + Past Participle (V3).\n"
                      "3. Subject of active becomes object of passive (usually with 'by').",
            "formulas": "- Present Indefinite: is/am/are + V3\n"
                        "- Past Indefinite: was/were + V3\n"
                        "- Present Continuous: is/am/are + being + V3\n"
                        "- Perfect Tenses: has/have/had + been + V3",
            "examples": [
                {
                    "question": "Change to Passive: 'He is playing cricket.'",
                    "solution": "**Step 1: Identify S, V, O.**\nS=He, V=is playing, O=cricket.\n\n**Step 2: Apply conversion.**\nPassive Subject: Cricket.\nVerb: is being played.\nObject with 'by': by him.\n\n**Final Result:** 'Cricket is being played by him.'"
                }
            ]
        },
        # --- General Intelligence & Reasoning ---
        {
            "name": "Syllogism",
            "theory": "### Syllogism\n"
                      "Syllogism is a form of logical reasoning where a conclusion is drawn from two or more given premises.\n\n"
                      "**Types of Statements**:\n"
                      "1. **All A are B** (Universal Affirmative)\n"
                      "2. **No A is B** (Universal Negative)\n"
                      "3. **Some A are B** (Particular Affirmative)\n"
                      "4. **Some A are not B** (Particular Negative)\n\n"
                      "**Method**: Use Venn Diagrams to visualize the relationships and test whether the conclusion MUST follow.",
            "formulas": "- All + All = All\n"
                        "- All + No = No\n"
                        "- All + Some = No Conclusion\n"
                        "- Some + No = Some Not",
            "examples": [
                {
                    "question": "Statements: All cars are wheels. Some wheels are spokes. Conclusion: I. Some cars are spokes. II. Some spokes are wheels.",
                    "solution": "**Step 1: Draw Venn Diagram.**\nCircle for 'Cars' inside 'Wheels'. Another circle for 'Spokes' overlapping with 'Wheels'.\n\n**Step 2: Check Conclusion I.**\nSpokes may or may not overlap with Cars. Not certain. So I doesn't follow.\n\n**Step 3: Check Conclusion II.**\nIf Some wheels are spokes, then Some spokes are DEFINITELY wheels. So II follows.\n\n**Final Result:** Only **Conclusion II follows**."
                }
            ]
        },
        # --- General Awareness ---
        {
            "name": "Indian Constitution: Preamble",
            "theory": "### Indian Constitution: Preamble\n"
                      "The Preamble is the introductory statement of the Constitution that sets out the guiding purpose and principles of the document.\n\n"
                      "**Key Words**:\n"
                      "- **Sovereign**: Free from external control.\n"
                      "- **Socialist**: Wealth shared equally (added by 42nd Amendment).\n"
                      "- **Secular**: No state religion (added by 42nd Amendment).\n"
                      "- **Democratic**: Rule by the people.\n"
                      "- **Republic**: Head of state is elected.",
            "formulas": "1. Adopted on: **26th Nov 1949**\n"
                        "2. Enforced on: **26th Jan 1950**\n"
                        "3. Amends: Only once (42nd Amendment, 1976)",
            "examples": [
                {
                    "question": "Which words were added to the Preamble by the 42nd Amendment?",
                    "solution": "**Step 1: Recall Amendment details.**\nThe 42nd Amendment (1976) added three words.\n\n**Step 2: Identify the words.**\nSocialist, Secular, and Integrity.\n\n**Final Result:** **Socialist, Secular, and Integrity**."
                }
            ]
        }
    ]

    for item in data:
        topic = StudyTopic.objects.filter(name__icontains=item["name"]).first()
        if not topic:
            # Create if doesn't exist (assuming we have a default subject or can find one)
            quant_subject = StudySubject.objects.filter(name__icontains="Quantitative").first()
            if quant_subject:
                topic = StudyTopic(name=item["name"], subject=quant_subject)
        
        if topic:
            topic.theory = item["theory"]
            topic.formulas = item["formulas"]
            topic.examples = item["examples"]
            topic.save()
            print(f"Updated/Created: {topic.name}")

if __name__ == "__main__":
    populate_concepts()
