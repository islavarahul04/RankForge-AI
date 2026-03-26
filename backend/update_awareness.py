from accounts.models import StudyTopic
import json

def update_theory():
    data = [
        {
            "name": "History",
            "theory": "### Indian History Overview\n"
                      "Indian history is classified into three main periods: Ancient, Medieval, and Modern.\n\n"
                      "**1. Ancient India**:\n"
                      "- **Indus Valley Civilization**: Known for advanced urban planning and drainage systems.\n"
                      "- **Vedic Period**: Composition of Vedas and development of caste system.\n"
                      "- **Buddhism & Jainism**: Emergence of new religions as a response to ritualistic practices.\n"
                      "- **Maurya & Gupta Empires**: Golden age of Indian art, science, and mathematics.\n\n"
                      "**2. Medieval India**:\n"
                      "- **Delhi Sultanate**: Established by Qutub-ud-din Aibak.\n"
                      "- **Mughal Empire**: Founded by Babur, reached its peak under Akbar and Shah Jahan.\n\n"
                      "**3. Modern India**:\n"
                      "- **British Colonial Rule**: Started with the Battle of Plassey (1757).\n"
                      "- **Freedom Struggle**: Key figures like Mahatma Gandhi, Subhash Chandra Bose, and Bhagat Singh led the movement for independence (1947).",
            "formulas": "", "examples": []
        },
        {
            "name": "Geography",
            "theory": "### World and Indian Geography\n"
                      "Geography deals with the study of lands, features, inhabitants, and phenomena of Earth.\n\n"
                      "**1. Physical Geography**:\n"
                      "- **Lithosphere**: Earth's crust and upper mantle.\n"
                      "- **Atmosphere**: Layer of gases surrounding the planet.\n"
                      "- **Hydrosphere**: All water bodies on Earth.\n\n"
                      "**2. Indian Geography**:\n"
                      "- **Himalayas**: The great mountain ranges in the North.\n"
                      "- **Indo-Gangetic Plains**: Fertile plains suitable for agriculture.\n"
                      "- **Peninsular Plateau**: Ancient tableland in the South.\n"
                      "- **Rivers**: Major rivers include Ganges, Brahmaputra, Indus, and peninsular rivers like Godavari and Krishna.",
            "formulas": "", "examples": []
        },
        {
            "name": "Neighboring Countries",
            "theory": "### India and Its Neighbors\n"
                      "India shares its land borders with seven countries and sea borders with two.\n\n"
                      "- **Pakistan**: Shared border in the West. Key issues include LOC and trade.\n"
                      "- **China**: Shared border in the North and East (LAC). Key issues include border disputes in Ladakh and Arunachal.\n"
                      "- **Nepal**: Open border policy; strong cultural ties.\n"
                      "- **Bhutan**: Landlocked kingdom; strategic ally.\n"
                      "- **Bangladesh**: Longest land border; cooperation in connectivity and security.\n"
                      "- **Myanmar**: Cultural and trade link to Southeast Asia.\n"
                      "- **Afghanistan**: Small border in POK region.\n"
                      "- **Sri Lanka & Maldives**: Maritime neighbors in the Indian Ocean.",
            "formulas": "", "examples": []
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
    update_theory()
