FROM python:3.11-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY app.py .
COPY utils.py .
COPY download_models.py .

# Download AI models from Google Drive at build time
RUN python download_models.py

# Expose port (Railway sets $PORT at runtime)
EXPOSE 5000

# Start with gunicorn
CMD ["sh", "-c", "gunicorn --bind 0.0.0.0:${PORT:-5000} --timeout 120 --workers 1 app:app"]
