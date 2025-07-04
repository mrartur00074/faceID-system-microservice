from kafka import KafkaConsumer, KafkaProducer
import json
import threading
from service import extract_number_from_base64  # твоя функция

consumer = KafkaConsumer(
    'number-reader-request',
    bootstrap_servers='kafka:9092',
    auto_offset_reset='earliest',
    value_deserializer=lambda x: x.decode('utf-8')
)

producer = KafkaProducer(
    bootstrap_servers='kafka:9092',
    value_serializer=lambda x: json.dumps(x).encode('utf-8')
)


def kafka_listener():
    for message in consumer:
        print("[Kafka] Получено изображение для распознавания")
        base64_image = message.value
        try:
            number = extract_number_from_base64(base64_image)
            response = {"number": number}
        except Exception as e:
            response = {"error": str(e)}

        producer.send('number-reader-response', response)
        print("[Kafka] Ответ отправлен:", response)