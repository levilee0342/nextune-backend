import mysql.connector
from mysql.connector import Error
from dotenv import load_dotenv
import os

load_dotenv()  # Load biến từ .env

def get_connection():
    return mysql.connector.connect(
        host=os.getenv("DB_HOST"),
        port=os.getenv("DB_PORT"),
        user=os.getenv("DB_USER"),
        password=os.getenv("DB_PASSWORD"),
        database=os.getenv("DB_NAME")
    )

def get_all_user_history():
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        sql = "SELECT user_id, track_id, played_at, skipped_at FROM user_listen_history"
        cursor.execute(sql)
        return cursor.fetchall()
    except Error as e:
        print("Error fetching history:", e)
        return []
    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

def get_user_history(user_id, limit=10):
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        sql = """
            SELECT track_id, played_at, skipped_at
            FROM user_listen_history
            WHERE user_id = %s
            ORDER BY played_at DESC
            LIMIT %s
        """
        cursor.execute(sql, (user_id, limit))
        return cursor.fetchall()
    except Error as e:
        print("Error fetching user history:", e)
        return []
    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

def get_all_tracks_with_genre(entity_type):
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)

        sql = """
            SELECT t.id, t.name, t.lyric, t.img_url, t.track_url,
                   GROUP_CONCAT(g.name SEPARATOR ' ') AS genres
            FROM track t
            LEFT JOIN track_genre tg ON t.id = tg.track_id
            LEFT JOIN genre g ON tg.genre_id = g.id
            WHERE t.status = 1 AND t.entity_type = %s
            GROUP BY t.id, t.name, t.lyric, t.img_url, t.track_url
        """
        cursor.execute(sql,(entity_type,))
        return cursor.fetchall()
    except Error as e:
        print("Error fetching tracks with genre:", e)
        return []
    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()
