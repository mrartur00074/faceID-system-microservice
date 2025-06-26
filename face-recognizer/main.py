from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from face_embedding_service import extract_embedding
from kafka_consumer import start_kafka_loop
from pydantic import BaseModel
from datetime import datetime
import logging
import os

# Настройка логирования
log_dir = "logs"
os.makedirs(log_dir, exist_ok=True)
log_file = os.path.join(log_dir, f"face_service_log_{datetime.now().strftime('%Y-%m-%d')}.log")
logging.basicConfig(
    filename=log_file,
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    encoding="utf-8"
)
logger = logging.getLogger()

# Создание FastAPI-приложения
app = FastAPI(
    title="Face Embedding Service",
    description="Извлечение эмбеддингов лиц из изображений",
    version="1.0.0"
)

# Разрешения для CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Модель запроса
class ImageRequest(BaseModel):
    base64_image: str

# POST-эндпоинт для ручного теста
@app.post("/get-embedding/")
def get_embedding(req: ImageRequest):
    logger.info("Запрос получен на /get-embedding/")
    embedding = extract_embedding(req.base64_image)

    if embedding is None:
        raise HTTPException(status_code=400, detail="Лицо не найдено или изображение некорректно.")
    return {"embedding": embedding}

# Старт Kafka-цикла при запуске приложения
@app.on_event("startup")
async def startup_event():
    start_kafka_loop()