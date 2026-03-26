import os
import re

LAYOUT_DIR = r"c:\Users\DELL\AndroidStudioProjects\RankForgeAI\app\src\main\res\layout"
DRAWABLE_DIR = r"c:\Users\DELL\AndroidStudioProjects\RankForgeAI\app\src\main\res\drawable"

REVERSE_REPLACEMENTS = {
    r'"@color/bg_main"': '"#F8F9FE"',
    r'"@color/text_primary"': '"#101820"',
    r'"@color/text_primary_alt"': '"#3A4D66"',
    r'"@color/text_secondary"': '"#5A6B80"',
    r'"@color/text_hint"': '"#8C98A4"',
    r'"@color/divider"': '"#F0F2F5"',
    r'"@color/bg_surface"': '"#FFFFFF"',
}

def process_dir(directory):
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".xml"):
                path = os.path.join(root, file)
                with open(path, "r", encoding="utf-8") as f:
                    content = f.read()
                
                original_content = content
                for pattern, replacement in REVERSE_REPLACEMENTS.items():
                    content = re.sub(pattern, replacement, content)
                
                if content != original_content:
                    with open(path, "w", encoding="utf-8") as f:
                        f.write(content)
                    print(f"Updated: {path}")

process_dir(LAYOUT_DIR)
process_dir(DRAWABLE_DIR)
print("Done.")
