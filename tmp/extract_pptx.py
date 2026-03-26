import zipfile
import xml.etree.ElementTree as ET
import os
import sys

def extract_text_from_pptx(pptx_path, output_path):
    if not os.path.exists(pptx_path):
        print(f"File {pptx_path} not found.")
        return

    with open(output_path, 'w', encoding='utf-8') as out_f:
        with zipfile.ZipFile(pptx_path, 'r') as zip_ref:
            # Get list of slide files
            slide_files = sorted([f for f in zip_ref.namelist() if f.startswith('ppt/slides/slide' ) and f.endswith('.xml')], 
                                key=lambda x: int(''.join(filter(str.isdigit, x))))
            
            for slide_file in slide_files:
                out_f.write(f"--- {slide_file} ---\n")
                with zip_ref.open(slide_file) as f:
                    tree = ET.parse(f)
                    root = tree.getroot()
                    
                    ns = {'a': 'http://schemas.openxmlformats.org/drawingml/2006/main',
                          'p': 'http://schemas.openxmlformats.org/presentationml/2006/main'}
                    
                    for t in root.findall('.//a:t', ns):
                        if t.text:
                            out_f.write(t.text + "\n")
                out_f.write("\n")

if __name__ == "__main__":
    pptx_path = r'c:\Users\DELL\AndroidStudioProjects\RankForgeAI\RankForge AI - slides.pptx'
    output_path = r'c:\Users\DELL\AndroidStudioProjects\RankForgeAI\tmp\extracted_slides.txt'
    extract_text_from_pptx(pptx_path, output_path)
    print(f"Extraction complete. Results saved to {output_path}")
