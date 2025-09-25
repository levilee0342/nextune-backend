import os
import mysql.connector
from mysql.connector import Error
from dotenv import load_dotenv

load_dotenv()

def get_connection():
    return mysql.connector.connect(
        host=os.getenv("DB_HOST"),
        port=os.getenv("DB_PORT"),
        user=os.getenv("DB_USER"),
        password=os.getenv("DB_PASSWORD"),
        database=os.getenv("DB_NAME")
    )

def get_all_albums_with_genres(entity_type):
    conn = cursor = None
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        sql = """
            SELECT 
                a.id,
                a.name,
                a.img_url,
                a.entity_type,
                u.name AS artist_name,
                GROUP_CONCAT(g.name SEPARATOR ' ') AS genres
            FROM albums a
            LEFT JOIN users u       ON u.id = a.artist_id
            LEFT JOIN album_genre ag ON ag.album_id = a.id
            LEFT JOIN genre g       ON g.id = ag.genre_id
            WHERE a.status = 'PUBLISHED' AND a.entity_type = %s
            GROUP BY a.id, a.name, a.img_url, a.entity_type, u.name
        """
        cursor.execute(sql, (entity_type,))
        return cursor.fetchall()
    except Error as e:
        print("Error fetching albums with genres:", e)
        return []
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

def get_all_user_album_history():
    conn = cursor = None
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        sql = """
            SELECT
                h.user_id,
                h.track_id,
                t.album_id,
                h.played_at,
                h.skipped_at
            FROM user_listen_history h
            JOIN track t ON t.id = h.track_id
        """
        cursor.execute(sql)
        return cursor.fetchall()
    except Error as e:
        print("Error fetching user album history:", e)
        return []
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

def get_user_album_history(user_id, limit=20):
    conn = cursor = None
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        sql = """
            SELECT
                t.album_id,
                h.track_id,
                h.played_at,
                h.skipped_at
            FROM user_listen_history h
            JOIN track t ON t.id = h.track_id
            WHERE h.user_id = %s
            ORDER BY h.played_at DESC
            LIMIT %s
        """
        cursor.execute(sql, (user_id, limit))
        return cursor.fetchall()
    except Error as e:
        print("Error fetching user album history:", e)
        return []
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
