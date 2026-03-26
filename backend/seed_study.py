import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rankforge_backend.settings')
django.setup()

from accounts.models import StudySubject, StudyTopic

def seed():
    subjects_data = [
        {
            "name": "Quantitative Aptitude",
            "icon_name": "ic_calculator",
            "icon_bg_drawable": "bg_subject_icon_blue",
            "progress_drawable": "bg_progress_bar_blue",
            "order": 1,
            "topics": [
                "Number Systems", "Computation of Whole Numbers", "Decimals and Fractions",
                "Relationship between numbers", "Fundamental arithmetical operations", "Percentages",
                "Ratio and Proportion", "Averages", "Interest", "Profit and Loss", "Discount",
                "Use of Tables and Graphs", "Mensuration", "Time and Distance", "Ratio and Time", "Time and Work"
            ]
        },
        {
            "name": "General Intelligence",
            "icon_name": "ic_pen_tool",
            "icon_bg_drawable": "bg_subject_icon_purple",
            "progress_drawable": "bg_progress_bar_purple",
            "order": 2,
            "topics": [
                "Semantic Analogy", "Symbolic/Number Analogy", "Figural Analogy", "Differences",
                "Word Building", "Coding and Decoding", "Numerical Operations", "Space Orientation",
                "Venn Diagrams", "Drawing Inferences"
            ]
        },
        {
            "name": "English Language",
            "icon_name": "ic_book_open",
            "icon_bg_drawable": "bg_subject_icon_orange",
            "progress_drawable": "bg_progress_bar_orange",
            "order": 3,
            "topics": [
                "Spot the Error", "Fill in the Blanks", "Synonyms/Homonyms", "Antonyms",
                "Spelling/Detecting Misspelt words", "Idioms & Phrases", "One word substitution",
                "Improvement of Sentences", "Active/Passive Voice of Verbs", "Comprehension Passage"
            ]
        },
        {
            "name": "General Awareness",
            "icon_name": "ic_globe_web",
            "icon_bg_drawable": "bg_subject_icon_green",
            "progress_drawable": "bg_progress_bar_green",
            "order": 4,
            "topics": [
                "History", "Culture", "Geography", "Economic Scene",
                "General policy", "Scientific Research", "Current Affairs",
                "Neighboring Countries"
            ]
        }
    ]

    for subj in subjects_data:
        subject, _ = StudySubject.objects.get_or_create(
            name=subj["name"],
            defaults={
                "icon_name": subj["icon_name"],
                "icon_bg_drawable": subj["icon_bg_drawable"],
                "progress_drawable": subj["progress_drawable"],
                "order": subj["order"]
            }
        )
        for idx, t_name in enumerate(subj["topics"]):
            StudyTopic.objects.get_or_create(
                subject=subject,
                name=t_name,
                defaults={"order": idx+1}
            )
            
    print("Study payload seeded successfully!")

if __name__ == '__main__':
    seed()
