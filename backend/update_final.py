from accounts.models import StudyTopic
import json

def update_final():
    data = [
        {
            "name": "Culture",
            "theory": "### Indian Culture and Heritage\n"
                      "Indian culture is one of the world's oldest and most diverse cultures. It is characterized by its colorful festivals, diverse languages, classical arts, and traditional values.\n\n"
                      "**1. Classical Dances**:\n"
                      "- **Bharatnatyam** (Tamil Nadu)\n"
                      "- **Kathak** (North India)\n"
                      "- **Kathakali** (Kerala)\n"
                      "- **Kuchipudi** (Andhra Pradesh)\n"
                      "- **Odissi** (Odisha)\n\n"
                      "**2. Festivals**:\n"
                      "- **National Festivals**: Independence Day, Republic Day, Gandhi Jayanti.\n"
                      "- **Religious Festivals**: Diwali, Eid, Christmas, Holi, Durga Puja.\n\n"
                      "**3. Architecture**:\n"
                      "- Features a mix of Hindu, Islamic, and European styles (e.g., Taj Mahal, Temples of Hampi, Victoria Memorial).",
            "formulas": "", "examples": []
        },
        {
            "name": "Analogy",
            "theory": "### Analogy Reasoning\n"
                      "Analogy means 'similarity'. In these problems, a particular relationship is given and another similar relationship has to be identified from the alternatives.\n\n"
                      "**Types of Analogies**:\n"
                      "1. **Word Analogy**: (e.g., Doctor : Hospital :: Teacher : School)\n"
                      "2. **Number Analogy**: (e.g., 2 : 4 :: 3 : 9)\n"
                      "3. **Letter Analogy**: (e.g., ABC : DEF :: GHI : JKL)\n\n"
                      "**Approach**:\n"
                      "- Identify the relation between the first pair.\n"
                      "- Apply the exact same logic to find the missing part of the second pair.",
            "formulas": "",
            "examples": [
                {
                    "question": "Choose the related word: Newspaper : Press :: Cloth : ?",
                    "solution": "**Step 1: Identify the relationship.**\nNewspaper is printed/produced in a Press.\n\n**Step 2: Apply the logic.**\nWhere is cloth produced? Cloth is produced in a Mill.\n\n**Final Result:** The related word is **Mill**."
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
    update_final()
