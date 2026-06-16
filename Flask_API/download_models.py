"""
Download AI model files from Google Drive into the models/ folder.
Run once before starting the server: python download_models.py
Safe to re-run — skips files that already exist.
"""

import os
import gdown

MODEL_DIR = os.path.join(os.path.dirname(__file__), "models")
os.makedirs(MODEL_DIR, exist_ok=True)

FILES = {
    "model_feed.joblib":       "17mAHideog6jhuTcaPH7x6JojLQlghPb5",
    "model_city.joblib":       "1jIV15MfZ0cr_7w34FVE32P3uI_u2p2wx",
    "model_experience.joblib": "1vCdVULNjxbf6UwHxeXLVIvbNVFMI49XC",
    "model_trips.joblib":      "10z7fXsJmxXqFttH7TA_jr5Z3MAJRm-n5",
    "encoders.joblib":         "1FNqaTfVbnz5eeiLdeQgwxfRS_W5WW4wd",
    "feature_columns.joblib":  "16D68wTGRJQq-v74yN1BNgMOmLBLZMsDD",
    "trips_catalog.csv":       "1nsEpKplsk7RjisUXol518AaSMPm0fjen",
}

for filename, file_id in FILES.items():
    dest = os.path.join(MODEL_DIR, filename)
    if os.path.exists(dest):
        print(f"  already present: {filename}")
    else:
        print(f"  downloading: {filename} ...")
        gdown.download(id=file_id, output=dest, quiet=False)

print("\nAll model files ready.")
