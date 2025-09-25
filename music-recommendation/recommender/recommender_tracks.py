import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from db.db_track import get_all_tracks_with_genre, get_all_user_history, get_user_history


def build_content_similarity(tracks):
    texts = []
    valid_tracks = []  # giữ lại danh sách track thực sự dùng được

    for t in tracks:
        lyric = t.get("lyric") or ""
        genres = t.get("genres") or ""
        name = t.get("name") or ""

        combined = f"{lyric} {genres} {name}".strip()

        # chỉ thêm nếu có dữ liệu thực sự
        if combined:
            texts.append(combined)
            valid_tracks.append(t)

    if not texts:  # không có dữ liệu để tính
        return None, []

    vectorizer = TfidfVectorizer(stop_words="english")
    tfidf_matrix = vectorizer.fit_transform(texts)
    similarity_matrix = cosine_similarity(tfidf_matrix, tfidf_matrix)
    return similarity_matrix, valid_tracks



def build_collaborative_matrix(history):
    df = pd.DataFrame(history)
    if df.empty:
        return None
    df["liked"] = df["skipped_at"].isnull().astype(int)
    user_track_matrix = df.pivot_table(
        index="user_id", columns="track_id", values="liked", fill_value=0
    )
    return user_track_matrix

def recommend_user(user_id, entity_type,topN=10, alpha=0.5):
    tracks = get_all_tracks_with_genre(entity_type)
    history = get_all_user_history()
    user_history = get_user_history(user_id)

    if not user_history:
        return {"message": "User chưa có lịch sử nghe nhạc"}

    # Content-based
    sim_matrix, filtered_tracks = build_content_similarity(tracks)

    # lấy track cuối user nghe
    last_track_id = user_history[0]["track_id"]

    content_rank = {}
    if sim_matrix is not None:
        id_list = [t["id"] for t in filtered_tracks]

        if last_track_id in id_list:
            track_index = id_list.index(last_track_id)
            scores = list(enumerate(sim_matrix[track_index]))
            scores = sorted(scores, key=lambda x: x[1], reverse=True)

            content_rank = {
                filtered_tracks[i]["id"]: score
                for i, score in scores
                if filtered_tracks[i]["id"] != last_track_id
            }
        # nếu last_track_id không nằm trong danh sách → bỏ qua content-based

    # Collaborative
    cf_rank = {}
    user_track_matrix = build_collaborative_matrix(history)
    if user_track_matrix is not None and user_id in user_track_matrix.index:
        user_sim = cosine_similarity(
            [user_track_matrix.loc[user_id]], user_track_matrix
        )[0]
        similar_users = sorted(
            list(zip(user_track_matrix.index, user_sim)),
            key=lambda x: x[1],
            reverse=True
        )[1:6]

        for sim_user, sim_score in similar_users:
            listened = user_track_matrix.loc[sim_user]
            for track_id, liked in listened.items():
                if liked > 0:
                    cf_rank[track_id] = cf_rank.get(track_id, 0) + sim_score

    # Kết hợp
    final_scores = {}
    for tid, score in content_rank.items():
        final_scores[tid] = alpha * score + (1 - alpha) * cf_rank.get(tid, 0)

    for tid, score in cf_rank.items():
        if tid not in final_scores:
            final_scores[tid] = (1 - alpha) * score

    final_sorted = sorted(final_scores.items(), key=lambda x: x[1], reverse=True)[:topN]

    id_to_track = {t["id"]: t for t in tracks}

    recommendations = []
    for tid, score in final_sorted:
        track = id_to_track.get(tid)
        if track:
            recommendations.append({
                "track_id": tid,
                "track_name": track["name"],  # lấy đúng name
                "genres": track.get("genres", ""),
                "lyric": track.get("lyric", ""),  # nếu cần lyric
                "img_url": track.get("img_url"),  # nếu cần ảnh
                "track_url": track.get("track_url"),
                "score": float(score)
            })
    print("Tracks:", len(tracks))
    print("History all:", len(history))
    print("User history:", user_history)
    print("Content rank:", len(content_rank))
    print("CF rank:", len(cf_rank))
    print("Final:", len(final_scores))

    return recommendations

def recommend_similar_tracks(track_id: str, entity_type: str, topN: int = 10, alpha: float = 0.6):
    """
    Gợi ý các track tương tự với track_id (cùng entity_type: SONGS/PODCASTS)
    Kết hợp: content-based + item-based collaborative
    """
    # 1) Lấy data
    tracks = get_all_tracks_with_genre(entity_type)
    history = get_all_user_history()

    if not tracks:
        return []

    # 2) CONTENT-BASED: TF-IDF trên lyric + genres + name
    sim_matrix, filtered_tracks = build_content_similarity(tracks)
    id_list = [t["id"] for t in filtered_tracks] if filtered_tracks else []

    content_rank = {}
    if sim_matrix is not None and track_id in id_list:
        idx = id_list.index(track_id)
        scores = list(enumerate(sim_matrix[idx]))
        # sắp xếp giảm dần, bỏ chính nó
        scores = sorted(scores, key=lambda x: x[1], reverse=True)
        for i, s in scores:
            other_id = filtered_tracks[i]["id"]
            if other_id != track_id:
                content_rank[other_id] = float(s)

    # 3) ITEM-BASED COLLAB: cosine theo cột track trong user x track matrix
    cf_rank = {}
    user_track_matrix = build_collaborative_matrix(history)  # index: user_id, columns: track_id
    if user_track_matrix is not None and track_id in user_track_matrix.columns:
        # vector cột cần so sánh (shape: n_users x 1)
        base_col = user_track_matrix[[track_id]]
        # cosine giữa base_col và tất cả cột (item-item)
        item_sims = cosine_similarity(base_col.T, user_track_matrix.T)[0]  # 1 x n_items
        all_track_ids = list(user_track_matrix.columns)

        # gom điểm
        for tid, s in zip(all_track_ids, item_sims):
            if tid != track_id:
                cf_rank[tid] = cf_rank.get(tid, 0.0) + float(s)

    # 4) KẾT HỢP
    final_scores = {}
    for tid, s in content_rank.items():
        final_scores[tid] = alpha * s + (1 - alpha) * cf_rank.get(tid, 0.0)
    for tid, s in cf_rank.items():
        if tid not in final_scores:
            final_scores[tid] = (1 - alpha) * s

    # 5) Trả topN (lọc chính nó, lọc track không cùng entity_type đã được xử lý từ đầu)
    final_sorted = sorted(final_scores.items(), key=lambda x: x[1], reverse=True)[:topN]

    id_to_track = {t["id"]: t for t in tracks}
    results = []
    for tid, score in final_sorted:
        tr = id_to_track.get(tid)
        if tr:
            results.append({
                "track_id": tid,
                "track_name": tr.get("name"),
                "duration": tr.get("duration"),
                "play_count": tr.get("play_count"),
                "genres": tr.get("genres", ""),
                "lyric": tr.get("lyric", ""),
                "img_url": tr.get("img_url"),
                "track_url": tr.get("track_url"),
                "score": float(score)
            })

    # debug nhẹ (tuỳ bạn để/log)
    print("[Similar] tracks:", len(tracks))
    print("[Similar] content_rank:", len(content_rank))
    print("[Similar] cf_rank:", len(cf_rank))
    print("[Similar] final:", len(final_scores))

    return results