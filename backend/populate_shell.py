from accounts.models import StudyTopic, StudySubject
import json

def populate():
    # Find Percentages topic
    topic = StudyTopic.objects.filter(name__icontains="Percentage").first()
    if not topic:
        subject = StudySubject.objects.first()
        if not subject:
            subject = StudySubject.objects.create(name="Quantitative Aptitude", icon_name="ic_calculator", icon_bg_drawable="bg_subject_icon_orange", progress_drawable="bg_progress_bar_orange")
        topic = StudyTopic.objects.create(name="Percentages", subject=subject)

    topic.theory = (
        "Percentage means 'per hundred'. It is a way of expressing a number as a fraction of 100. "
        "It is often denoted using the percent sign, '%'. \n\n"
        "To find the percentage of a number, we multiply the number by the percentage and then divide by 100."
    )
    topic.formulas = (
        "1. Percentage = (Part / Whole) × 100\n"
        "2. Percentage Increase = (Increase / Original Value) × 100\n"
        "3. Percentage Decrease = (Decrease / Original Value) × 100"
    )
    topic.examples = [
        {
            "question": "If a student scores 450 out of 600, what is the percentage of his marks?",
            "solution": "Percentage = (450 / 600) * 100 = 75%"
        },
        {
            "question": "A price of an item increases from $50 to $60. Find the percentage increase.",
            "solution": "Increase = 60 - 50 = 10. \nPercentage Increase = (10 / 50) * 100 = 20%"
        }
    ]
    topic.save()
    print(f"Successfully updated topic: {topic.name}")

populate()
