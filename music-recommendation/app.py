from recommender.recommender_albums import recommend_albums
from recommender.recommender_playlists import recommend_playlists
from recommender.recommender_tracks import recommend_user, recommend_similar_tracks

from fastapi import FastAPI, APIRouter
from starlette.middleware.cors import CORSMiddleware

router = APIRouter(prefix="/reco")

@router.get("/recommend-tracks/{entity_type}/{user_id}")
def recommend(entity_type: str, user_id: str, topN: int = 10):
    return recommend_user(user_id, entity_type, topN=topN)

@router.get("/recommend-albums/{entity_type}/{user_id}")
def api_recommend_albums(entity_type: str, user_id: str, topN: int = 10):
    return recommend_albums(user_id=user_id, entity_type=entity_type, topN=topN)

@router.get("/recommend-playlists/{user_id}")
def api_recommend_playlists(user_id: str, topN: int = 10):
    return recommend_playlists(user_id=user_id, topN=topN)

@router.get("/similar-tracks/{entity_type}/{track_id}")
def api_similar_tracks(entity_type: str, track_id: str, topN: int = 10):
    return recommend_similar_tracks(track_id=track_id, entity_type=entity_type, topN=topN)

@router.get("/health")
def health():
    return {"status": "ok"}

app = FastAPI()
app.include_router(router)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "https://nextune.ddnsgeek.com",
        "http://127.0.0.1:5500",
      # "http://localhost:5500",
        "http://localhost:5173",
        "http://localhost:3000"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# uvicorn app:app --host 0.0.0.0 --port 8000 --reload