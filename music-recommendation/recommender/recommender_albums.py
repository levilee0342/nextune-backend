# recommender_albums.py
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from db.db_album import (
    get_all_albums_with_genres,
    get_all_user_album_history,
    get_user_album_history,
)

def _build_album_texts(albums):
    """
    Chuẩn hoá text cho Content-based: name + artist_name + genres
    """
    texts = []
    valid = []
    for a in albums:
        name = (a.get("name") or "").strip()
        artist = (a.get("artist_name") or "").strip()
        genres = (a.get("genres") or "").strip()
        combined = " ".join([x for x in [name, artist, genres] if x]).strip()
        if combined:
            texts.append(combined)
            valid.append(a)
    return texts, valid

def _content_scores_for_last_album(albums):
    texts, valid_albums = _build_album_texts(albums)
    if not texts:
        return None, [], None, None

    vectorizer = TfidfVectorizer(stop_words="english")
    tfidf_matrix = vectorizer.fit_transform(texts)
    id_list = [a["id"] for a in valid_albums]
    return tfidf_matrix, id_list, vectorizer, valid_albums

def _build_user_album_matrix(all_album_history):
    df = pd.DataFrame(all_album_history)
    if df.empty:
        return None

    # Tạo cột liked cho từng lượt nghe (track)
    df["liked"] = df["skipped_at"].isnull().astype(int)

    # Gộp về cấp (user_id, album_id): còn 1 nếu có ít nhất 1 lượt liked
    agg = (
        df.groupby(["user_id", "album_id"])["liked"]
        .max()  # có một lần liked là đủ
        .reset_index()
    )

    user_album = agg.pivot_table(
        index="user_id", columns="album_id", values="liked", fill_value=0, aggfunc="max"
    )

    return user_album

def recommend_albums(user_id: str, entity_type: str, topN: int = 10, alpha: float = 0.6):
    """
       Gợi ý album hybrid:
         - Content-based: cosine TF-IDF trên (name + artist_name + genres),
           dùng album user vừa nghe gần nhất làm query.
         - CF user-based: tìm user gần nhất (cosine), cộng dồn điểm album họ 'liked'.
         - Kết hợp điểm: alpha * content + (1 - alpha) * cf
       """
    # 1) Dữ liệu
    albums = get_all_albums_with_genres(entity_type)
    if not albums:
        return {"message": "Không có album phù hợp entity_type hoặc dữ liệu rỗng."}

    all_album_hist = get_all_user_album_history()
    user_hist = get_user_album_history(user_id)
    if not user_hist:
        return {"message": "User chưa có lịch sử nghe (album)!"}

    # 2) Content-based: album gần nhất mà user đã nghe
    #    (user_hist đã ORDER BY played_at DESC, lấy album_id của dòng đầu tiên)
    last_album_id = user_hist[0].get("album_id")

    tfidf_matrix, id_list, vectorizer, valid_albums = _content_scores_for_last_album(albums)
    content_rank = {}
    if tfidf_matrix is not None and last_album_id in id_list:
        last_idx = id_list.index(last_album_id)
        vec_last = tfidf_matrix[last_idx]
        # cosine của vec_last với toàn bộ tfidf matrix (O(N))
        scores = cosine_similarity(vec_last, tfidf_matrix).ravel()

        # sắp xếp giảm dần, loại chính nó
        ranked = sorted(
            [(i, s) for i, s in enumerate(scores) if id_list[i] != last_album_id],
            key=lambda x: x[1],
            reverse=True,
        )
        content_rank = {id_list[i]: float(s) for i, s in ranked}

    # 3) CF user-based
    cf_rank = {}
    user_album_matrix = _build_user_album_matrix(all_album_hist)
    if user_album_matrix is not None and user_id in user_album_matrix.index:
        # cosine của user_id với tất cả users
        user_sim = cosine_similarity(
            [user_album_matrix.loc[user_id]], user_album_matrix
        )[0]

        # lấy top 5 user gần nhất (bỏ chính nó)
        similar_users = sorted(
            list(zip(user_album_matrix.index, user_sim)),
            key=lambda x: x[1],
            reverse=True,
        )[1:6]

        for sim_user, sim_score in similar_users:
            listened = user_album_matrix.loc[sim_user]
            # listened: Series index là album_id, value 0/1
            for album_id, liked in listened.items():
                if liked > 0:
                    cf_rank[album_id] = cf_rank.get(album_id, 0.0) + float(sim_score)

    # 4) Hợp nhất điểm
    final_scores = {}
    # a) album có điểm content
    for aid, c in content_rank.items():
        final_scores[aid] = alpha * c + (1 - alpha) * cf_rank.get(aid, 0.0)

    # b) album chỉ có điểm CF
    for aid, c in cf_rank.items():
        if aid not in final_scores:
            final_scores[aid] = (1 - alpha) * c

    # 5) Sắp xếp & dựng response
    final_sorted = sorted(final_scores.items(), key=lambda x: x[1], reverse=True)[:topN]
    id_to_album = {a["id"]: a for a in albums}

    recommendations = []
    for aid, score in final_sorted:
        album = id_to_album.get(aid)
        if album:
            recommendations.append({
                "album_id": aid,
                "album_name": album.get("name"),
                "artist_name": album.get("artist_name"),
                "genres": album.get("genres") or "",
                "img_url": album.get("img_url"),
                "entity_type": album.get("entity_type"),
                "score": float(score),
            })

    # Debug (có thể đổi sang logging)
    print("Albums:", len(albums))
    print("User album history (recent):", user_hist[:3], "...")
    print("Content rank size:", len(content_rank))
    print("CF rank size:", len(cf_rank))
    print("Final size:", len(final_scores))

    return recommendations
