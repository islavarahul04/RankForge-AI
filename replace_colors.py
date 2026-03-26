import os
import re

LAYOUT_DIR = r"c:\Users\DELL\AndroidStudioProjects\RankForgeAI\app\src\main\res\layout"
DRAWABLE_DIR = r"c:\Users\DELL\AndroidStudioProjects\RankForgeAI\app\src\main\res\drawable"

REPLACEMENTS = {
    r'"#F8F9FE"': '"@color/bg_main"',
    r'"#101820"': '"@color/text_primary"',
    r'"#3A4D66"': '"@color/text_primary_alt"',
    r'"#5A6B80"': '"@color/text_secondary"',
    r'"#8C98A4"': '"@color/text_hint"',
    r'"#F0F2F5"': '"@color/divider"',
    r'(android:background(Tint)?|app:cardBackgroundColor|android:fillColor)="\#FFFFFF"': r'\1="@color/bg_surface"',
    # A generic fallback if some white backgrounds weren't matched above, but be careful with icons!
}

def process_dir(directory):
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".xml"):
                path = os.path.join(root, file)
                with open(path, "r", encoding="utf-8") as f:
                    content = f.read()
                
                original_content = content
                for pattern, replacement in REPLACEMENTS.items():
                    content = re.sub(pattern, replacement, content)
                
                if content != original_content:
                    with open(path, "w", encoding="utf-8") as f:
                        f.write(content)
                    print(f"Updated: {path}")

process_dir(LAYOUT_DIR)
process_dir(DRAWABLE_DIR)
print("Done.")
