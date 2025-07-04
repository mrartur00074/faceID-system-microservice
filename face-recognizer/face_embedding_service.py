import base64
import io
from PIL import Image
import numpy as np
import cv2
import insightface
import logging

logger = logging.getLogger()

face_model = insightface.app.FaceAnalysis(name="buffalo_l")
face_model.prepare(ctx_id=-1)  # CPU

def get_largest_face(faces):
    def area(face): return (face.bbox[2] - face.bbox[0]) * (face.bbox[3] - face.bbox[1])
    return max(faces, key=area)

def extract_embedding(base64_str: str):
    try:
        image = Image.open(io.BytesIO(base64.b64decode(base64_str)))
        image_np = np.array(image)

        # RGBA → BGR или RGB → BGR
        if image_np.shape[-1] == 4:
            image_np = cv2.cvtColor(image_np, cv2.COLOR_RGBA2BGR)
        else:
            image_np = cv2.cvtColor(image_np, cv2.COLOR_RGB2BGR)

        faces = face_model.get(image_np)
        if not faces:
            logger.warning("Лицо не найдено")
            return None

        largest_face = get_largest_face(faces)
        return largest_face.embedding.tolist()

    except Exception as e:
        logger.exception("Ошибка при обработке изображения")
        return None