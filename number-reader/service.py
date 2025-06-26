import re
import base64
import numpy as np
import cv2
from paddleocr import PaddleOCR

ocr = PaddleOCR(lang="en")  # инициализируется один раз

def extract_number_from_base64(base64_string: str) -> str:
    def try_recognize(image):
        result = ocr.ocr(image, cls=False)
        recognized = []
        for line in result:
            for word_info in line:
                text, confidence = word_info[1]
                if re.fullmatch(r'\d+', text):
                    recognized.append((text, confidence))
        return recognized

    print("Запущена функция")
    # Декодируем изображение
    image_data = base64.b64decode(base64_string)
    np_arr = np.frombuffer(image_data, np.uint8)
    image = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    if image is None:
        raise ValueError("Ошибка: не удалось декодировать изображение")

    recognized_numbers = try_recognize(image)

    print("По центру обрезка")
    if not recognized_numbers:
        height, width, _ = image.shape
        crop_size = 0.6
        x1 = int(width * (1 - crop_size) / 2)
        y1 = int(height * (1 - crop_size) / 2)
        x2 = int(width * (1 + crop_size) / 2)
        y2 = int(height * (1 + crop_size) / 2)
        cropped_image = image[y1:y2, x1:x2]
        recognized_numbers = try_recognize(cropped_image)

    if recognized_numbers:
        best_match = max(recognized_numbers, key=lambda x: x[1])
        return best_match[0]
    else:
        return "Не удалось распознать число"