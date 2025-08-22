FROM ubuntu:22.04

ARG VERSION=1.0.0
LABEL version=$VERSION

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        python3.10 \
        python3-pip \
        python3-dev \
        gcc \
        g++ \
        libffi-dev \
        libssl-dev \
        libgl1 \
        libglib2.0-0 \
        libsm6 \
        libxext6 \
        libxrender-dev \
        netcat-openbsd \
        libjansson-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN ln -s /usr/bin/python3.10 /usr/local/bin/python

WORKDIR /app

COPY . .

RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

COPY wait-for-kafka.sh /wait-for-kafka.sh
RUN chmod +x /wait-for-kafka.sh

CMD ["/wait-for-kafka.sh", "kafka", "uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8002"]