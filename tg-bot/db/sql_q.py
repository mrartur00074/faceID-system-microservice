import sqlite3
from pathlib import Path

class DB:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(DB, cls).__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return
        self.connection = sqlite3.connect(Path(__file__).parent.parent / 'db.sqlite')
        self.cursor = self.connection.cursor()
        self._initialized = True

    def create_tables(self):
        '''Создание таблиц'''
        self.cursor.execute('''
            CREATE TABLE IF NOT EXISTS teachers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tg_id INTEGER
            )
        ''')
        self.cursor.execute('''
            CREATE TABLE IF NOT EXISTS groups (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                group_id INTEGER
            )
        ''')
        self.cursor.execute('''
            CREATE TABLE IF NOT EXISTS admin (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                admin_id INTEGER
            )
        ''')
        self.cursor.execute('''
            CREATE TABLE IF NOT EXISTS log_group
            (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                group_id INTEGER
            )
        ''')
        self.connection.commit()

    def get_teachers(self):
        '''Получение всех учителей'''
        self.cursor.execute("SELECT * FROM teachers")
        return self.cursor.fetchall()

    def new_teacher(self, data: dict):
        '''Добавление нового учителя'''
        self.cursor.execute(
            "INSERT INTO teachers (tg_id) VALUES (:tg_id)",
            data
        )
        self.connection.commit()

    def get_groups(self):
        '''Получение всех групп'''
        self.cursor.execute("SELECT * FROM groups")
        return self.cursor.fetchall()

    def new_group(self, data: dict):
        '''Добавление новой группы'''
        self.cursor.execute(
            "INSERT INTO groups (group_id) VALUES (:group_id)",
            data
        )
        self.connection.commit()

    def get_admin(self):
        '''Получение администратора'''
        self.cursor.execute("SELECT * FROM admin")
        return self.cursor.fetchone()

    def set_admin(self, data: dict):
        '''Очистка таблицы и установка нового администратора'''
        self.cursor.execute("DELETE FROM admin")
        self.cursor.execute("INSERT INTO admin (admin_id) VALUES (:admin_id)", data)
        self.connection.commit()

    def clear_table(self, table_name: str):
        '''Полная очистка таблицы по имени'''
        self.cursor.execute(f"DELETE FROM {table_name}")
        self.connection.commit()

    def delete_teacher(self, tg_id: int):
        '''Удаление учителя по Telegram ID'''
        self.cursor.execute("DELETE FROM teachers WHERE tg_id = ?", (tg_id,))
        self.connection.commit()

    def set_log_group(self, group_id: int):
        '''Установка ID группы для логов (перезапись)'''
        self.cursor.execute("DELETE FROM log_group")
        self.cursor.execute("INSERT INTO log_group (group_id) VALUES (?)", (group_id,))
        self.connection.commit()

    def get_log_group(self):
        '''Получение ID группы для логов'''
        self.cursor.execute("SELECT group_id FROM log_group LIMIT 1")
        result = self.cursor.fetchone()
        return result[0] if result else None