FROM python:3.10-slim

RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential gcc g++ python3-dev libffi-dev libssl-dev \
    netcat-openbsd && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

RUN pip install --no-cache-dir -r requirements.txt

COPY wait-for-kafka.sh /wait-for-kafka.sh
RUN chmod +x /wait-for-kafka.sh

CMD ["/wait-for-kafka.sh", "kafka", "uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001"]