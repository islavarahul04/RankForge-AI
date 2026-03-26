from accounts.models import StudyTopic
import json

def update_percentages():
    topic = StudyTopic.objects.filter(name__icontains="Percentage").first()
    if not topic:
        return

    topic.theory = (
        "### What is Percentage?\n"
        "The word 'Percentage' comes from the Latin phrase 'per centum', which means 'per hundred'. "
        "It is a way of expressing a fraction with a denominator of 100. The symbol '%' is used to denote percentage.\n\n"
        "#### 1. Basic Conversion\n"
        "- **Fraction to Percentage**: Multiply the fraction by 100 and add the '%' sign. (e.g., 1/4 = 1/4 × 100 = 25%)\n"
        "- **Percentage to Fraction**: Remove the '%' sign and divide by 100. (e.g., 20% = 20/100 = 1/5)\n"
        "- **Decimal to Percentage**: Multiply by 100 and add the '%' sign. (e.g., 0.75 = 75%)\n\n"
        "#### 2. Percentage as a Tool of Comparison\n"
        "Percentages allow us to compare values of different scales on a common base (100). "
        "For example, scoring 80/100 (80%) is same as 160/200 (80%)."
    )

    topic.formulas = (
        "1. **Basic Value**: Value = (Percentage / 100) × Base\n\n"
        "2. **Percentage Change**: % Change = [(Final Value - Initial Value) / Initial Value] × 100\n"
        "   - Positive result means Percentage Increase.\n"
        "   - Negative result means Percentage Decrease.\n\n"
        "3. **Value after % Change**: New Value = Original Value × (1 ± R/100)\n"
        "   - Use '+' for increase and '-' for decrease.\n\n"
        "4. **Successive Percentage Change**: If a value changes by a% and then by b%, the net change is:\n"
        "   Net % Change = [a + b + (ab/100)]%\n\n"
        "5. **Product Stability**: If Price increases by P%, then to keep expenditure constant, consumption must decrease by:\n"
        "   Decrease % = [P / (100 + P)] × 100%\n\n"
        "6. **Population Growth**: Population after n years = P × (1 + R/100)ⁿ\n"
        "7. **Depreciation**: Value after n years = P × (1 - R/100)ⁿ"
    )

    topic.examples = [
        {
            "question": "A student's salary was increased by 20% and then decreased by 10%. What is the net percentage change in his salary?",
            "solution": "**Step 1: Identify the changes.**\nLet initial salary = ₹100.\nFirst change (a) = +20% (Increase)\nSecond change (b) = -10% (Decrease)\n\n**Step 2: Apply the Net Percentage Change formula.**\nNet % Change = [a + b + (ab/100)]%\nNet % Change = [+20 + (-10) + ((20 × -10) / 100)]%\nNet % Change = [20 - 10 + (-200/100)]%\nNet % Change = [10 - 2]% = +8%\n\n**Final Result:** The net change is an **8% increase**."
        },
        {
            "question": "The price of sugar rises by 25%. By what percentage must a housewife reduce her consumption so that her expenditure on sugar does not increase?",
            "solution": "**Step 1: Identify the price increase.**\nPrice Increase (P) = 25%\n\n**Step 2: Understand the relationship.**\nExpenditure = Price × Consumption. To keep Expenditure constant, if Price increases, Consumption must decrease.\n\n**Step 3: Apply the Product Stability formula.**\nReduction % = [P / (100 + P)] × 100%\nReduction % = [25 / (100 + 25)] × 100%\nReduction % = [25 / 125] × 100%\nReduction % = (1/5) × 100% = 20%\n\n**Final Result:** The housewife must reduce consumption by **20%**."
        },
        {
            "question": "A town's population increases by 10% annually. If the current population is 10,000, what will be the population after 2 years?",
            "solution": "**Step 1: Identify given values.**\nCurrent Population (P) = 10,000\nGrowth Rate (R) = 10% per annum\nTime (n) = 2 years\n\n**Step 2: Apply the Population Growth formula.**\nPopulation after n years = P × (1 + R/100)ⁿ\nPopulation = 10,000 × (1 + 10/100)²\nPopulation = 10,000 × (1.1)²\n\n**Step 3: Calculate the value.**\nPopulation = 10,000 × (1.1 × 1.1)\nPopulation = 10,000 × 1.21 = 12,100\n\n**Final Result:** The population after 2 years will be **12,100**."
        }
    ]
    topic.save()
    print("Percentages updated successfully.")

if __name__ == "__main__":
    update_percentages()
