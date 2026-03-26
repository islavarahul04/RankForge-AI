from accounts.models import StudyTopic
import json

def populate_bulk():
    data = [
        # QUANTITATIVE APTITUDE
        {
            "name": "Profit and Loss",
            "theory": "### Profit and Loss Fundamentals\n"
                      "Profit and Loss are terms used to identify whether a deal is profitable or not in day-to-day life. "
                      "They depend on two main prices: Cost Price (CP) and Selling Price (SP).\n\n"
                      "- **Cost Price (CP)**: The price at which an article is purchased.\n"
                      "- **Selling Price (SP)**: The price at which an article is sold.\n"
                      "- **Marked Price (MP)**: The price mentioned on the article (list price).\n"
                      "- **Discount**: Reduction offered on the Marked Price.",
            "formulas": "1. **Profit** = SP - CP (when SP > CP)\n"
                        "2. **Loss** = CP - SP (when CP > SP)\n"
                        "3. **Profit %** = (Profit / CP) × 100\n"
                        "4. **Loss %** = (Loss / CP) × 100\n"
                        "5. **Selling Price** = CP × [(100 + Profit%) / 100] OR CP × [(100 - Loss%) / 100]\n"
                        "6. **Discount** = MP - SP\n"
                        "7. **Discount %** = (Discount / MP) × 100",
            "examples": [
                {
                    "question": "An article is bought for ₹800 and sold for ₹1000. Find the profit percentage.",
                    "solution": "**Step 1: Identify CP and SP.**\nCP = ₹800, SP = ₹1000\n\n**Step 2: Calculate Profit.**\nProfit = SP - CP = 1000 - 800 = ₹200\n\n**Step 3: Calculate Profit Percentage.**\nProfit % = (Profit / CP) × 100\nProfit % = (200 / 800) × 100 = 25%\n\n**Final Result:** The profit percentage is **25%**."
                },
                {
                    "question": "A dealer marks his goods 20% above the cost price and allows a discount of 10%. Find his net profit percentage.",
                    "solution": "**Step 1: Assume a Cost Price.**\nLet CP = ₹100.\n\n**Step 2: Calculate Marked Price (MP).**\nMP = CP + 20% of CP = 100 + 20 = ₹120.\n\n**Step 3: Calculate Selling Price (SP).**\nDiscount = 10% of MP = 10% of 120 = ₹12.\nSP = MP - Discount = 120 - 12 = ₹108.\n\n**Step 4: Calculate Net Profit.**\nProfit = SP - CP = 108 - 100 = ₹8.\nProfit % = (8 / 100) × 100 = 8%.\n\n**Final Result:** The net profit is **8%**."
                }
            ]
        },
        # GENERAL INTELLIGENCE
        {
            "name": "Blood Relations",
            "theory": "### Understanding Blood Relations\n"
                      "Blood relations problems test your ability to trace connections between family members. "
                      "The key is to represent generations and genders clearly.\n\n"
                      "- **Generations**: Represent different generations vertically (Grandparents > Parents > Self > Children).\n"
                      "- **Gender Symbols**: Usually, '+' or a square is used for Male, and '-' or a circle for Female.\n"
                      "- **Spouse Symbol**: A double line (=) is used for married couples.\n"
                      "- **Sibling Symbol**: A single horizontal line (-) is used for siblings.",
            "formulas": "Symbols to remember:\n"
                        "1. Father's/Mother's Brother = Uncle\n"
                        "2. Father's/Mother's Sister = Aunt\n"
                        "3. Uncle's/Aunt's Son/Daughter = Cousin\n"
                        "4. Brother's/Sister's Son = Nephew\n"
                        "5. Brother's/Sister's Daughter = Niece\n"
                        "6. Son's/Daughter's Wife/Husband = Daughter-in-law/Son-in-law",
            "examples": [
                {
                    "question": "Pointing to a photograph, a man said, 'I have no brother or sister but that man's father is my father's son.' Whose photograph was it?",
                    "solution": "**Step 1: Analyze the last part.**\n'My father's son' -> Since the man has no brother or sister, he himself is his father's son.\n\n**Step 2: Substitute in the sentence.**\n'That man's father is [my father's son]' becomes 'That man's father is ME'.\n\n**Step 3: Conclusion.**\nIf the man in the photo's father is the speaker, then the photo is of his own son.\n\n**Final Result:** The photograph was of his **Son**."
                }
            ]
        },
        # ENGLISH LANGUAGE
        {
            "name": "Active/Passive Voice of Verbs",
            "theory": "### Active vs Passive Voice\n"
                      "- **Active Voice**: The subject performs the action. (e.g., 'The cat chased the mouse.')\n"
                      "- **Passive Voice**: The subject receives the action. (e.g., 'The mouse was chased by the cat.')\n\n"
                      "**Rules for Conversion:**\n"
                      "1. The object of the active sentence becomes the subject of the passive sentence.\n"
                      "2. Use the appropriate form of the verb 'to be' (am, is, are, was, were, been, being) + the past participle (V3) of the main verb.\n"
                      "3. Use the word 'by' before the doer (agent) of the action.",
            "formulas": "Tense Conversion Table:\n"
                        "1. **Present Simple**: write/writes -> is/am/are written\n"
                        "2. **Past Simple**: wrote -> was/were written\n"
                        "3. **Present Continuous**: is writing -> is being written\n"
                        "4. **Future Simple**: will write -> will be written\n"
                        "5. **Present Perfect**: has written -> has been written",
            "examples": [
                {
                    "question": "Change the following sentence to Passive Voice: 'The mechanic repaired the car.'",
                    "solution": "**Step 1: Identify Subject, Verb, and Object.**\nSubject: The mechanic\nVerb: repaired (Past Simple)\nObject: the car\n\n**Step 2: Interchange Subject and Object.**\nNew Subject: The car\n\n**Step 3: Apply Verb Rule (Past Simple).**\n'repaired' becomes 'was repaired'.\n\n**Step 4: Form the final sentence.**\n'The car was repaired by the mechanic.'\n\n**Final Result:** The car was repaired by the mechanic."
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
    populate_bulk()
