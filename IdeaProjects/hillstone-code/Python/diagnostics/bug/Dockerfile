FROM python:3.11-slim
WORKDIR /app
COPY bug_core.py bug_app.py requirements.txt ./
RUN pip install --no-cache-dir flet-web requests beautifulsoup4
EXPOSE 8551
CMD ["python", "bug_app.py", "--web"]
