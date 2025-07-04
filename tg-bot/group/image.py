from aiogram import Router, types
from config import bot, db
from .request_to_api import send_file
import base64

image_router = Router()

@image_router.message()
async def pic(message: types.Message):
    print(f"Пришло сообщение в чат {message.chat.id} от {message.from_user.id}")

    ADMIN_CHAT_IDS = [396952302]
    allowed_chats = [group[1] for group in db.get_groups()] + ADMIN_CHAT_IDS
    print(f"Допустимые чаты: {allowed_chats}")

    if message.chat.id not in allowed_chats:
        print("Чат не разрешён")
        return

    admin_id = db.get_admin()[1] if db.get_admin() else None
    teacher_ids = [t[1] for t in db.get_teachers()]
    print(teacher_ids)

    if message.from_user.id != admin_id and message.from_user.id not in teacher_ids:
        return

    if not message.photo:
        return

    log_group_id = db.get_log_group()

    try:
        photo = message.photo[-1].file_id
        file = await bot.get_file(photo)
        await bot.download_file(file.file_path, "group/student.png")

        with open("group/student.png", "rb") as photo_file:
            photo_base64 = base64.b64encode(photo_file.read()).decode("utf-8")

        applicant_id = message.caption if message.caption and message.caption.isdigit() else None
        response = send_file(photo_base64, applicant_id)

        message_text = response.get("message", "❓ Неизвестный ответ от сервера")
        status_code = response.get("status_code", "❓")

        await message.reply(text=message_text)

        if log_group_id:
            await bot.send_message(
                chat_id=log_group_id,
                text=f"📥 Фото от [{message.from_user.full_name}](tg://user?id={message.from_user.id}) обработано.\n"
                     f"Ответ сервера: {message_text} (Код: {status_code})",
                parse_mode="Markdown"
            )

    except Exception as e:
        print(f"Ошибка: {e}")
        if log_group_id:
            await bot.send_message(
                chat_id=log_group_id,
                text=f"❌ Ошибка при обработке фото от [{message.from_user.full_name}](tg://user?id={message.from_user.id}):\n`{str(e)}`",
                parse_mode="Markdown"
            )
