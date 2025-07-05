FROM python:3.10-slim

ARG VERSION=1.0.0
LABEL version=$VERSION

ENV DEBIAN_FRONTEND=noninteractive

RUN find /etc/apt/ -name '*.list' -exec sed -i 's|http://deb.debian.org/debian|http://ftp.debian.org/debian|g' {} +

RUN bash -c '\
    apt-get update && \
    for i in 1 2 3 4 5; do \
      apt-get install -y --no-install-recommends --fix-missing \
        build-essential gcc g++ python3-dev libffi-dev libssl-dev netcat-openbsd && \
      if [ $? -eq 0 ]; then break; fi; \
      echo "Попытка $i не удалась, повторяем через 5 секунд..."; \
      sleep 5; \
    done && \
    apt-get clean && rm -rf /var/lib/apt/lists/* \
'

WORKDIR /app
COPY . .

RUN pip install --no-cache-dir -r requirements.txt

RUN pip uninstall -y onnxruntime-gpu onnxruntime && pip install onnxruntime

COPY wait-for-kafka.sh /wait-for-kafka.sh
RUN chmod +x /wait-for-kafka.sh

CMD ["/wait-for-kafka.sh", "kafka", "uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001", "--reload"]
