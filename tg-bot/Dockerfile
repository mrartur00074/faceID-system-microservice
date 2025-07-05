FROM python:3.10-slim

ARG VERSION=1.0.0
LABEL version=$VERSION

WORKDIR /app
COPY . .

RUN pip install --no-cache-dir -r requirements.txt

CMD ["python", "main.py"]