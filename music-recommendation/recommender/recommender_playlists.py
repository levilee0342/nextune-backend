# recommender_playlists.py
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from db.db_playlist import (
    get_all_playlists_with_genres,
    get_all_user_playlist_interactions,
    get_user_recent_playlist_interactions,
)

def _build_playlist_texts(playlists):
    """
    Văn bản cho Content-based: name + description + genres
    """
    texts, valid = [], []
    for p in playlists:
        name = (p.get("name") or "").strip()
        desc = (p.get("description") or "").strip()
        genres = (p.get("genres") or "").strip()
        combined = " ".join([x for x in [name, desc, genres] if x]).strip()
        if combined:
            texts.append(combined)
            valid.append(p)
    return texts, valid

def _fit_tfidf(playlists):
    texts, valid = _build_playlist_texts(playlists)
    if not texts:
        return None, [], None, None
    vectorizer = TfidfVectorizer(stop_words="english")
    tfidf = vectorizer.fit_transform(texts)
    id_list = [p["id"] for p in valid]
    return tfidf, id_list, vectorizer, valid

def _build_user_playlist_matrix(interactions):
    """
    Từ interactions [{user_id, playlist_id, weight}], gom về ma trận user x playlist với giá trị >=0.
    Nếu cùng (user, playlist) có cả explicit và implicit → lấy tổng (sum).
    """
    df = pd.DataFrame(interactions)
    if df.empty:
        return None

    # bảo vệ cột weight
    if "weight" not in df.columns:
        df["weight"] = 1.0

    agg = (
        df.groupby(["user_id", "playlist_id"])["weight"]
        .sum()
        .reset_index()
    )

    mat = agg.pivot_table(
        index="user_id", columns="playlist_id", values="weight", fill_value=0, aggfunc="sum"
    )

    return mat

def recommend_playlists(user_id: str, topN: int = 10, alpha: float = 0.6):
    """
    Hybrid:
      - Content-based: TF-IDF (name+description+genres), lấy playlist user tương tác gần nhất (save hoặc listen) làm query.
      - CF user-based: similarity giữa users dựa trên ma trận interactions (save=1.0, listen-implicit=0.3, có thể tinh chỉnh).
      - Hợp nhất: alpha * content + (1 - alpha) * cf
    """
    # 1) dữ liệu
    playlists = get_all_playlists_with_genres()
    if not playlists:
        return {"message": "Không có playlist public/PUBLISHED hoặc dữ liệu rỗng."}

    recent = get_user_recent_playlist_interactions(user_id)
    if not recent:
        return {"message": "User chưa có tương tác playlist (save hoặc nghe gián tiếp)."}

    all_interactions = get_all_user_playlist_interactions()

    # 2) Content-based
    last_pid = recent[0].get("playlist_id")  # cái gần nhất
    tfidf, id_list, vectorizer, valid_playlists = _fit_tfidf(playlists)
    content_rank = {}
    if tfidf is not None and last_pid in id_list:
        last_idx = id_list.index(last_pid)
        vec_last = tfidf[last_idx]
        scores = cosine_similarity(vec_last, tfidf).ravel()
        ranked = sorted(
            [(i, s) for i, s in enumerate(scores) if id_list[i] != last_pid],
            key=lambda x: x[1],
            reverse=True
        )
        content_rank = {id_list[i]: float(s) for i, s in ranked}

    # 3) CF user-based
    cf_rank = {}
    user_playlist_matrix = _build_user_playlist_matrix(all_interactions)
    if user_playlist_matrix is not None and user_id in user_playlist_matrix.index:
        user_sim = cosine_similarity(
            [user_playlist_matrix.loc[user_id]], user_playlist_matrix
        )[0]
        similar_users = sorted(
            list(zip(user_playlist_matrix.index, user_sim)),
            key=lambda x: x[1],
            reverse=True
        )[1:6]  # top5, bỏ chính mình

        for sim_user, sim_score in similar_users:
            vec = user_playlist_matrix.loc[sim_user]
            for pid, w in vec.items():
                if w > 0:
                    cf_rank[pid] = cf_rank.get(pid, 0.0) + float(sim_score) * float(w)

    # 4) hợp nhất
    final_scores = {}
    for pid, c in content_rank.items():
        final_scores[pid] = alpha * c + (1 - alpha) * cf_rank.get(pid, 0.0)

    for pid, c in cf_rank.items():
        if pid not in final_scores:
            final_scores[pid] = (1 - alpha) * c

    # 5) response
    final_sorted = sorted(final_scores.items(), key=lambda x: x[1], reverse=True)[:topN]
    id_to_playlist = {p["id"]: p for p in playlists}

    recs = []
    for pid, score in final_sorted:
        p = id_to_playlist.get(pid)
        if p:
            recs.append({
                "playlist_id": pid,
                "name": p.get("name"),
                "description": p.get("description") or "",
                "img_url": p.get("img_url"),
                "genres": p.get("genres") or "",
                "total_tracks": p.get("total_tracks"),
                "total_followers": p.get("total_followers"),
                "score": float(score)
            })

    # debug
    print("Playlists:", len(playlists))
    print("Recent interaction head:", recent[:3], "...")
    print("Content rank size:", len(content_rank))
    print("CF rank size:", len(cf_rank))
    print("Final size:", len(final_scores))

    return recs
