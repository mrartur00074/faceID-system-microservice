import asyncio
import json
from aiokafka import AIOKafkaConsumer, AIOKafkaProducer
from face_embedding_service import extract_embedding

KAFKA_BOOTSTRAP_SERVERS = "kafka:9092"
REQUEST_TOPIC = "face-recognizer-request"
RESPONSE_TOPIC = "face-recognizer-response"

producer = None

async def kafka_consumer_task():
    global producer
    consumer = AIOKafkaConsumer(
        REQUEST_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="face-recognizer-group"
    )
    producer = AIOKafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS)

    await consumer.start()
    await producer.start()
    try:
        async for msg in consumer:
            try:
                base64_image = msg.value.decode("utf-8")
                embedding = extract_embedding(base64_image)
                if embedding is None:
                    response = json.dumps({"error": "Лицо не найдено"})
                else:
                    response = json.dumps({"embedding": embedding})
            except Exception as e:
                response = json.dumps({"error": str(e)})

            await producer.send_and_wait(RESPONSE_TOPIC, response.encode("utf-8"))
    finally:
        await consumer.stop()
        await producer.stop()

def start_kafka_loop():
    loop = asyncio.get_event_loop()
    loop.create_task(kafka_consumer_task())