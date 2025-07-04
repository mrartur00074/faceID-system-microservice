from aiogram import Router, types
from admin.key_boards import start_kb, admin_kb
from config import admin_command, db
from aiogram.filters import Command
from aiogram import Bot

admin_router = Router()

@admin_router.message(Command("start"))
async def start(message: types.Message):
    print(f"Пользователь вызвал /start, его ID: {message.from_user.id}")
    if db.get_admin() and message.from_user.id == db.get_admin()[1]:
        await message.answer(f"Здравствуйте! {message.from_user.full_name}", reply_markup=admin_kb)
    elif message.from_user.id in [i[1] for i in db.get_teachers()]:
        await message.answer(f"Здравствуйте! {message.from_user.full_name}", reply_markup=start_kb)


# @admin_router.message(Command(admin_command))
# async def new_admin(message: types.Message):
#     if not db.get_admin()[1] if db.get_admin() else False:
#         db.new_teacher({"tg_id":message.from_user.id})
#         db.set_admin({"admin_id":message.from_user.id})
#         await message.answer("Вы назначены админом данной системы", reply_markup=admin_kb)


@admin_router.message(Command("admin"))
async def new_admin(message: types.Message, bot: Bot):
    args = message.text.strip().split()
    if len(args) > 1 and args[1] == admin_command:
        admin_data = db.get_admin()  # Получаем данные о текущем администраторе
        if not admin_data or not admin_data[1]:  # Если админ не назначен
            db.new_teacher({"tg_id": message.from_user.id})
            db.set_admin({"admin_id": message.from_user.id})
            await message.answer("Вы назначены админом данной системы", reply_markup=admin_kb)
        else:
            chat = await bot.get_chat(admin_data[1])
            full_name = chat.full_name
            username = f"@{chat.username}" if chat.username else "—"

            await message.answer(
                f"👤 Админ системы:\n"
                f"▪️ Имя: {full_name}\n"
                f"▪️ Username: {username}\n")
    else:
        await message.answer("Неверный ключ. Используйте: /admin <ключ>")


@admin_router.message(Command("whoisadmin"))
async def who_is_admin(message: types.Message, bot: Bot):
    admin_data = db.get_admin()  # Ожидаем: (id, tg_id)

    # Выводим данные, чтобы проверить что именно возвращает get_admin
    print(f"admin_data: {admin_data}")

    if admin_data and admin_data[1]:  # Проверяем наличие tg_id админа
        tg_id = admin_data[1]
        try:
            chat = await bot.get_chat(tg_id)
            full_name = chat.full_name
            username = f"@{chat.username}" if chat.username else "—"
            await message.answer(
                f"👤 Админ системы:\n"
                f"▪️ Имя: {full_name}\n"
                f"▪️ Username: {username}\n"
                f"▪️ Telegram ID: {tg_id}"
            )
        except Exception as e:
            await message.answer(f"Ошибка при получении профиля админа: {e}")
    else:
        await message.answer("⚠️ Админ ещё не назначен.")