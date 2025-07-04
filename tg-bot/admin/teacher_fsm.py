from aiogram import Router, F, types
from aiogram.fsm.state import State, StatesGroup
from aiogram.fsm.context import FSMContext
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton
from config import bot, db
from .key_boards import cancel_kb, user_kb, admin_kb

teacher_router = Router()

class Teachers(StatesGroup):
    teacher_reg = State()
    teacher_del = State()

def teachers_kb():
    teachers = db.get_teachers()
    buttons = [
        [InlineKeyboardButton(text=str(t[1]), callback_data=f"del_teacher:{t[1]}")]
        for t in teachers if t[1] != db.get_admin()[1]  # не показываем админа
    ]
    return InlineKeyboardMarkup(inline_keyboard=buttons)

@teacher_router.callback_query(F.data == "remove_teacher")
async def remove_teacher(call: types.CallbackQuery, state: FSMContext):
    if call.from_user.id != db.get_admin()[1]:
        await call.answer("Нет доступа", show_alert=True)
        return

    await call.message.delete()
    await call.message.answer("Выберите учителя для удаления:", reply_markup=teachers_kb())
    await state.set_state(Teachers.teacher_del)

@teacher_router.callback_query(F.data.startswith("del_teacher:"), Teachers.teacher_del)
async def delete_selected_teacher(call: types.CallbackQuery, state: FSMContext):
    user_id = int(call.data.split(":")[1])
    db.delete_teacher(user_id)
    await call.message.delete()
    await call.message.answer(f"Учитель с ID {user_id} был удалён.")
    await state.clear()

@teacher_router.callback_query(F.data == "cancel")
async def cancel_registration(call: types.CallbackQuery, state: FSMContext):
    await call.message.delete()
    await state.clear()
    await call.message.answer("Процесс приостановлен")


@teacher_router.callback_query(F.data == "add_teacher")
async def new_teacher(call: types.CallbackQuery, state: FSMContext):
    if call.from_user.id != db.get_admin()[1]:
        await call.answer("Нет доступа", show_alert=True)
        return

    await call.message.delete()
    await state.set_state(Teachers.teacher_reg)
    await call.message.answer("Нажмите на кнопку и выберите учителя", reply_markup=user_kb)
    await call.message.answer("Идёт процесс добавления нового учителя", reply_markup=cancel_kb)


@teacher_router.message(Teachers.teacher_reg)
async def user_shared(message: types.Message, state: FSMContext):
    await message.delete()

    if not message.user_shared:
        await message.answer("Отправлено сообщение не верного формата.")
        return

    user_id = message.user_shared.user_id

    if user_id in [i[1] for i in db.get_teachers()]:
        await message.answer(f"Учитель с ID {user_id} уже существует.")
    else:
        db.new_teacher({"tg_id": user_id})
        await message.answer(f"Учитель с ID {user_id} был добавлен.")

    await state.clear()
    await message.answer(f"Здравствуйте! {message.from_user.full_name}")


@teacher_router.callback_query(F.data == "clear_teachers")
async def clear_teachers(call: types.CallbackQuery):
    if call.from_user.id != db.get_admin()[1]:
        await call.answer("Нет доступа", show_alert=True)
        return

    await call.message.delete()
    db.clear_table("teachers")
    db.new_teacher({"tg_id": db.get_admin()[1]})
    await call.message.answer("Список учителей был очищен.")
    await call.message.answer(f"Здравствуйте! {call.from_user.full_name}", reply_markup=admin_kb)

@teacher_router.callback_query(F.data == "list_teachers")
async def list_teachers(call: types.CallbackQuery):
    if call.from_user.id != db.get_admin()[1]:
        await call.answer("Нет доступа", show_alert=True)
        return

    teachers = db.get_teachers()
    if not teachers:
        await call.message.answer("Список учителей пуст.")
    else:
        text = "Список учителей:\n"
        for i, teacher in enumerate(teachers, 1):
            tg_id = teacher[1]
            text += f"{i}. TG ID: {tg_id}\n"
        await call.message.answer(text)