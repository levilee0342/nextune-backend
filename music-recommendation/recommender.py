import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from db import get_all_user_history, get_user_history, get_all_tracks_with_genre


from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

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

    return recommendations