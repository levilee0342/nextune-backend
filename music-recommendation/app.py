from fastapi import FastAPI
from recommender import recommend_user

app = FastAPI()

@app.get("/recommend/{entity_type}/{user_id}")
def recommend(entity_type: str, user_id: str, topN: int = 10):
    return recommend_user(user_id, entity_type, topN=topN)
