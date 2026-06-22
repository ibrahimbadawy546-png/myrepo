# RTP Analyzer PoC — PPTX Generation

This folder contains a Python script to generate an inspirational demo PowerPoint for the RTP Analyzer PoC.

Files included:
- generate_pptx.py : Script that builds RTP_Analyzer_PoC_Achievements_Demo.pptx using python-pptx and matplotlib.

How to use:
1. Clone the repo or navigate to this directory.
2. (Optional) Create a Python virtual environment: python -m venv .venv
3. Activate the environment and install dependencies:
   - pip install python-pptx matplotlib
4. Run the script:
   - python generate_pptx.py
5. The script will create RTP_Analyzer_PoC_Achievements_Demo.pptx in the current directory and a pptx_assets folder with generated chart images.

Notes:
- The script uses the analysis metrics provided in the PoC notes (triggers, endpoints, FMB sizes, etc.).
- If you want the PPTX committed directly, run the script locally and commit the generated PPTX back to the repo or tell me to push the binary.
