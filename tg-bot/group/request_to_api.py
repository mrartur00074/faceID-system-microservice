import requests
from config import Api_address

def send_file(base64_data, applicant_id):
    data = {
        "base64": base64_data,
        "applicantId": applicant_id
    }

    url = Api_address + "/applicants/add"

    print("Отправка на:" + url)

    try:
        response = requests.post(url, json=data)
    except requests.RequestException as e:
        return {
            "status_code": 500,
            "response_json": {"error": f"Ошибка при отправке запроса: {str(e)}"}
        }

    try:
        response_json = response.json()
    except ValueError:
        response_json = {"error": "Сервер вернул не JSON", "raw_text": response.text}

    print(f"[{response.status_code}] {response_json}")

    if "error" in response_json:
        message = f"❌ Ошибка: {response_json['error']}"
    elif response_json.get("status") == "success":
        message = f"✅ Успех: {response_json.get('message', 'Абитуриент добавлен')}"
    else:
        message = f"⚠ Неожиданный ответ: {response_json}"

    return {
        "status_code": response.status_code,
        "response_json": response_json,
        "message": message
    }
