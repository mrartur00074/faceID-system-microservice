import sys
import asyncio

# Для Windows + aiohttp
if sys.platform.startswith("win") and sys.version_info >= (3, 8):
    asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())

import logging
from config import bot, dp, set_commands, db
from group.image import image_router
from admin import admin_router, group_router, teacher_router, fsm_router, admin_fsm_router

async def on_startup(dispatcher):
    db.create_tables()
    print('Бот вышел в онлайн')

async def main():
    await set_commands()
    dp.include_router(admin_router)
    dp.include_router(admin_fsm_router)
    dp.include_router(fsm_router)
    dp.include_router(group_router)
    dp.include_router(teacher_router)
    dp.include_router(image_router)
    await dp.start_polling(bot, skip_updates=True)

if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)
    dp.startup.register(on_startup)
    asyncio.run(main())