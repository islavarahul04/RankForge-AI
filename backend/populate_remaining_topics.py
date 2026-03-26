from accounts.models import StudyTopic
import json

def populate_remaining():
    # Helper to update topics by list
    def update_set(names, theory_tpl, formulas_tpl, examples_tpl):
        for name in names:
            topic = StudyTopic.objects.filter(name=name).first()
            if topic and not topic.theory:
                topic.theory = theory_tpl.replace("{name}", name)
                topic.formulas = formulas_tpl.replace("{name}", name)
                topic.examples = examples_tpl
                topic.save()
                print(f"Updated: {topic.name}")

    # 1. QUANTITATIVE APTITUDE GENERICS
    quant_topics = [
        "Computation of Whole Numbers", "Decimals and Fractions", "Relationship between numbers",
        "Fundamental arithmetical operations", "Ratio and Proportion", "Averages", "Interest",
        "Discount", "Use of Tables and Graphs", "Mensuration", "Time and Distance", "Ratio and Time"
    ]
    update_set(quant_topics, 
        "### {name} Concepts\n{name} is a fundamental concept in Quantitative Aptitude used to solve complex numerical problems efficiently.",
        "1. Standard {name} formula: Value = result / base.\n2. Shortcut: Use unit method for faster calculation.",
        [{"question": "Sample {name} problem?", "solution": "Apply standard steps to reach the result."}]
    )

    # 2. GENERAL INTELLIGENCE GENERICS
    gi_topics = [
        "Semantic Analogy", "Symbolic/Number Analogy", "Figural Analogy", "Differences",
        "Word Building", "Coding and Decoding", "Numerical Operations", "Space Orientation",
        "Venn Diagrams", "Drawing Inferences", "Analogy", "Classification", "Coding-Decoding"
    ]
    update_set(gi_topics,
        "### {name} Reasoning\n{name} tests your logical reasoning and pattern recognition skills.",
        "1. Identify the pattern.\n2. Apply the same logic to the question set.",
        [{"question": "Solve this {name} sequence.", "solution": "Identify pattern -> Verify -> Solution."}]
    )

    # 3. ENGLISH LANGUAGE GENERICS
    eng_topics = [
        "Spot the Error", "Fill in the Blanks", "Synonyms/Homonyms", "Antonyms",
        "Spelling/Detecting Misspelt words", "Idioms & Phrases", "One word substitution",
        "Improvement of Sentences", "Comprehension Passage"
    ]
    update_set(eng_topics,
        "### {name} in English\nHaving a strong grasp of {name} is essential for scoring well in the English section.",
        "1. Focus on vocabulary.\n2. Master basic grammar rules.",
        [{"question": "Identify {name} in sentence.", "solution": "Analyze context -> Identify category -> Apply rule."}]
    )

    # 4. GENERAL AWARENESS GENERICS
    ga_topics = [
        "History", "Culture", "Geography", "Economic Scene", "General policy",
        "Scientific Research", "Current Affairs", "Neighboring Countries"
    ]
    update_set(ga_topics,
        "### {name} Facts\n{name} covers a wide range of facts and events that are significant for competitive exams.",
        "1. Focus on dates and timelines.\n2. Understand geographical/political significance.",
        [{"question": "Basic {name} fact?", "solution": "Recall fact -> Relate to context."}]
    )

if __name__ == "__main__":
    populate_remaining()
