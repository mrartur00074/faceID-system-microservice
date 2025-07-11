import asyncio
import base64
import json
from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
from service import extract_number_from_base64
from aiokafka import AIOKafkaConsumer, AIOKafkaProducer

app = FastAPI()

KAFKA_BOOTSTRAP_SERVERS = "kafka:9092"
REQUEST_TOPIC = "number-reader-request"
RESPONSE_TOPIC = "number-reader-response"

producer: AIOKafkaProducer = None


class ImageBase64Request(BaseModel):
    image_base64: str


@app.on_event("startup")
async def startup_event():
    global producer
    producer = AIOKafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS)
    await producer.start()

    loop = asyncio.get_event_loop()
    loop.create_task(kafka_consumer_task())


@app.on_event("shutdown")
async def shutdown_event():
    if producer:
        await producer.stop()


@app.post("/recognize-base64/")
def recognize_number_from_base64_api(req: ImageBase64Request):
    try:
        number = extract_number_from_base64(req.image_base64)
        return {"number": number}
    except Exception as e:
        return {"error": str(e)}


@app.post("/recognize-file/")
async def recognize_number_from_file(file: UploadFile = File(...)):
    try:
        image_bytes = await file.read()
        base64_str = base64.b64encode(image_bytes).decode("utf-8")
        number = extract_number_from_base64(base64_str)
        return {"number": number}
    except Exception as e:
        return {"error": str(e)}


async def kafka_consumer_task():
    consumer = AIOKafkaConsumer(
        REQUEST_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="number-reader-group"
    )
    await consumer.start()
    try:
        async for msg in consumer:
            try:
                base64_image = msg.value.decode("utf-8")
                number = extract_number_from_base64(base64_image)
                response = json.dumps({"number": number})
            except Exception as e:
                response = json.dumps({"error": str(e)})

            await producer.send_and_wait(RESPONSE_TOPIC, response.encode("utf-8"))
    finally:
        await consumer.stop()