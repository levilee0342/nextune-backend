# db_playlist.py
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

def get_all_playlists_with_genres():
    conn = cursor = None
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        sql = """
            SELECT 
                p.id,
                p.name,
                p.description,
                p.img_url,
                p.total_tracks,
                p.total_followers,
                p.is_public,
                GROUP_CONCAT(g.name SEPARATOR ' ') AS genres
            FROM playlist p
            LEFT JOIN playlist_genre pg ON pg.playlist_id = p.id
            LEFT JOIN genre g          ON g.id = pg.genre_id
            WHERE p.status = 'PUBLISHED' AND (p.is_public = 1 OR p.is_public IS NULL)
            GROUP BY p.id, p.name, p.description, p.img_url, p.total_tracks, p.total_followers, p.is_public
        """
        cursor.execute(sql)
        return cursor.fetchall()
    except Error as e:
        print("Error fetching playlists with genres:", e)
        return []
    finally:
        if cursor: cursor.close()
        if conn: conn.close()

def get_user_playlist_saves(user_id, limit=50):
    conn = cursor = None
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        sql = """
            SELECT ps.playlist_id, ps.created_at
            FROM playlist_save ps
            WHERE ps.user_id = %s
            ORDER BY ps.created_at DESC
            LIMIT %s
        """
        cursor.execute(sql, (user_id, limit))
        return cursor.fetchall()
    except Error as e:
        print("Error fetching user playlist saves:", e)
        return []
    finally:
        if cursor: cursor.close()
        if conn: conn.close()

def get_all_user_playlist_interactions():
    conn = cursor = None
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)

        # 1) explicit saves
        cursor.execute("""
            SELECT user_id, playlist_id, 1.0 AS weight
            FROM playlist_save
        """)
        explicit_rows = cursor.fetchall() or []
        cursor.execute("""
            SELECT DISTINCT h.user_id, tc.playlist_id, 0.3 AS weight
            FROM user_listen_history h
            JOIN track_collection tc ON tc.track_id = h.track_id
        """)
        implicit_rows = cursor.fetchall() or []

        return explicit_rows + implicit_rows
    except Error as e:
        print("Error fetching user playlist interactions:", e)
        return []
    finally:
        if cursor: cursor.close()
        if conn: conn.close()

def get_user_recent_playlist_interactions(user_id, limit=20):
    conn = cursor = None
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)

        # 1) explicit (save)
        cursor.execute("""
            SELECT ps.playlist_id, ps.created_at AS ts, 'SAVE' AS source
            FROM playlist_save ps
            WHERE ps.user_id = %s
            ORDER BY ps.created_at DESC
            LIMIT %s
        """, (user_id, limit))
        saves = cursor.fetchall() or []

        if saves:
            return saves

        # 2) implicit (nghe track thuộc playlist)
        cursor.execute("""
            SELECT tc.playlist_id, h.played_at AS ts, 'LISTEN' AS source
            FROM user_listen_history h
            JOIN track_collection tc ON tc.track_id = h.track_id
            WHERE h.user_id = %s
            ORDER BY h.played_at DESC
            LIMIT %s
        """, (user_id, limit))
        listens = cursor.fetchall() or []

        return listens
    except Error as e:
        print("Error fetching user recent playlist interactions:", e)
        return []
    finally:
        if cursor: cursor.close()
        if conn: conn.close()
