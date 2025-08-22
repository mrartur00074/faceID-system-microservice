import sys
import asyncio
import py_eureka_client.eureka_client as eureka_client
import os
from tenacity import retry, stop_after_attempt, wait_exponential

if sys.platform.startswith("win") and sys.version_info >= (3, 8):
    asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())

EUREKA_SERVER_URL = os.getenv("EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE", "http://localhost:8761/eureka/")
APP_NAME = os.getenv("APP_NAME", "tg-bot")
APP_PORT = int(os.getenv("APP_PORT", 8003))
APP_HOST = os.getenv("APP_HOST", "tg-bot")

@retry(stop=stop_after_attempt(10), wait=wait_exponential(multiplier=1, min=4, max=10))
async def register_with_eureka():
    try:
        await eureka_client.init_async(
            eureka_server=EUREKA_SERVER_URL,
            app_name=APP_NAME,
            instance_port=APP_PORT,
            instance_host=APP_HOST,
            instance_ip=APP_HOST,
            renewal_interval_in_secs=30,
            duration_in_secs=90
        )
        logging.info(f"Successfully registered with Eureka as {APP_NAME}")
        return True
    except Exception as e:
        logging.warning(f"Failed to register with Eureka, retrying: {e}")
        raise

import logging
from config import bot, dp, set_commands, db
from group.image import image_router
from admin import admin_router, group_router, teacher_router, fsm_router, admin_fsm_router

async def on_startup(dispatcher):
    db.create_tables()
    try:
        await register_with_eureka()
        print('Бот вышел в онлайн и зарегистрирован в Eureka')
    except Exception as e:
        logging.error(f"Final failure to register with Eureka: {e}")
        print('Бот вышел в онлайн, но не смог зарегистрироваться в Eureka')
    print('Бот вышел в онлайн')

async def on_shutdown(dispatcher):
    try:
        eureka_client.stop()
        logging.info("Deregistered from Eureka")
    except Exception as e:
        logging.error(f"Failed to deregister from Eureka: {e}")

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