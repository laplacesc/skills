FROM python:3.11-slim

WORKDIR /app
COPY rd_task_core.py rd_task_app.py requirements.txt ./

RUN pip install --no-cache-dir flet-web requests pandas

EXPOSE 8550

CMD ["python", "rd_task_app.py", "--web"]
