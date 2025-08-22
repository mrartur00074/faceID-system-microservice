import asyncio
import base64
import json
from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
from service import extract_number_from_base64
from aiokafka import AIOKafkaConsumer, AIOKafkaProducer
import py_eureka_client.eureka_client as eureka_client
import os

app = FastAPI()

KAFKA_BOOTSTRAP_SERVERS = "kafka:9092"
REQUEST_TOPIC = "number-reader-request"
RESPONSE_TOPIC = "number-reader-response"

producer: AIOKafkaProducer = None

EUREKA_SERVER_URL = os.getenv("EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE", "http://localhost:8761/eureka/")
APP_NAME = os.getenv("APP_NAME", "number-reader")
APP_PORT = int(os.getenv("APP_PORT", 8002))
APP_HOST = os.getenv("APP_HOST", "number-reader")

@app.on_event("startup")
async def startup_event():
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER_URL,
        app_name=APP_NAME,
        instance_port=APP_PORT,
        instance_host=APP_HOST,
        instance_ip=APP_HOST,
        renewal_interval_in_secs=30,
        duration_in_secs=90
    )


class ImageBase64Request(BaseModel):
    base64: str


@app.on_event("startup")
async def startup_event():
    loop = asyncio.get_event_loop()


@app.post("/recognize-base64/")
def recognize_number_from_base64_api(req: ImageBase64Request):
    try:
        number = extract_number_from_base64(req.base64)
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