from aiogram import Router, F, types
from aiogram.fsm.state import State, StatesGroup
from aiogram.fsm.context import FSMContext
from config import db
from admin.key_boards import cancel_kb, group_kb, admin_kb

group_router = Router()


class Groups(StatesGroup):
    new_group = State()
    set_log_group = State()

@group_router.callback_query(F.data == "cancel")
async def cancel_registration(call: types.CallbackQuery, state: FSMContext):
    await call.message.delete()
    await state.clear()
    await call.message.answer("Процесс приостановлен.")
    await call.message.answer(f"Здравствуйте! {call.from_user.full_name}", reply_markup=admin_kb)


@group_router.callback_query(F.data == "add_group")
async def new_group(call: types.CallbackQuery, state: FSMContext):
    admin_data = db.get_admin()
    if not admin_data or call.from_user.id != admin_data[1]:
        await call.answer("Нет доступа", show_alert=True)
        return

    await call.message.delete()
    await state.set_state(Groups.new_group)
    await call.message.answer("Нажмите на кнопку и выберите чат", reply_markup=group_kb)
    await call.message.answer("Идёт процесс добавления нового чата", reply_markup=cancel_kb)


@group_router.callback_query(F.data == "clear_groups")
async def clear_groups(call: types.CallbackQuery):
    admin_data = db.get_admin()
    if not admin_data or call.from_user.id != admin_data[1]:
        await call.answer("Нет доступа", show_alert=True)
        return

    await call.message.delete()
    db.clear_table("groups")
    await call.message.answer("Список групп был очищен.")
    await call.message.answer(f"Здравствуйте! {call.from_user.full_name}", reply_markup=admin_kb)


@group_router.message(Groups.new_group)
async def chat_shared(message: types.Message, state: FSMContext):
    await message.delete()

    if not message.chat_shared:
        await message.answer("Отправлено сообщение не верного формата.")
        return

    chat_id = message.chat_shared.chat_id

    if chat_id in [i[1] for i in db.get_groups()]:
        await message.answer(f"Группа с ID {chat_id} уже существует.")
    else:
        db.new_group({"group_id": chat_id})
        await message.answer(f"Группа с ID {chat_id} была добавлена.")

    await state.clear()

    # Завершающее сообщение
    await message.answer(f"Здравствуйте! {message.from_user.full_name}", reply_markup=admin_kb)

@group_router.callback_query(F.data == "view_groups")
async def view_groups(call: types.CallbackQuery):
    admin_data = db.get_admin()
    if not admin_data or call.from_user.id != admin_data[1]:
        await call.answer("Нет доступа", show_alert=True)
        return

    await call.message.delete()

    groups = db.get_groups()
    if not groups:
        await call.message.answer("Список групп пуст.")
        return

    print(groups)

    group_list = "\n".join([f"• {group[1]}" for group in groups])
    await call.message.answer(f"📋 Список добавленных групп:\n{group_list}", reply_markup=admin_kb)

@group_router.callback_query(F.data == "set_log_group")
async def set_log_group(call: types.CallbackQuery, state: FSMContext):
    admin_data = db.get_admin()
    if not admin_data or call.from_user.id != admin_data[1]:
        await call.answer("Нет доступа", show_alert=True)
        return

    await call.message.delete()
    await state.set_state(Groups.set_log_group)
    await call.message.answer("Нажмите кнопку и выберите чат для логирования", reply_markup=group_kb)
    await call.message.answer("Ожидается выбор лог-группы...", reply_markup=cancel_kb)

@group_router.message(Groups.set_log_group, F.chat_shared)
async def process_log_group_selection(message: types.Message, state: FSMContext):
    group_id = message.chat_shared.chat_id
    db.set_log_group(group_id)

    await message.answer(f"Лог-группа успешно установлена: `{group_id}`", parse_mode="Markdown")
    await state.clear()