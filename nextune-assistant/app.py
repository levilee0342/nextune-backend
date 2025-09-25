from fastapi import FastAPI, APIRouter
from pydantic import BaseModel
import os, json, re
import google.generativeai as genai
from dotenv import load_dotenv
from starlette.middleware.cors import CORSMiddleware

load_dotenv()
genai.configure(api_key=os.getenv("GOOGLE_API_KEY"))
model = genai.GenerativeModel("gemini-2.0-flash")

SYSTEM_PROMPT = """
You are Nextune Voice Assistant. The user may speak English or Vietnamese.
Return ONLY valid JSON. No extra text/markdown.
Schema:
{
  "intent": "pause|play|next|prev|seek|set_volume|play_by_name|answer|clarify|search|play_artist|play_album|play_playlist|play_genre|like_song|add_to_playlist",
  "entities": { ... },
  "answer": "<short answer for Q&A>",
  "question": "<short clarify question>"
}
- For seek: entities={ "offset_ms": int, "relative": true|false }
- For set_volume: entities={ "level": 0..100 }
- For play_by_name: entities={ "title": string, "artist": string? }
If ambiguous → intent="clarify" with "question".
If open-domain Q&A → intent="answer" with short "answer".
"""

class NluReq(BaseModel):
    text: str
    locale: str | None = None

def extract_json(raw: str):
    raw = raw.strip()
    try:
        return json.loads(raw)
    except:
        m = re.search(r"\{.*\}", raw, flags=re.S)
        if not m:
            return {"intent":"answer","answer": raw}
        return json.loads(m.group(0))

router = APIRouter(prefix="/nlu")

@router.get("/health")
def health():
    return {"status": "ok"}

@router.post("")  
def nlu(req: NluReq):
    prompt = f"{SYSTEM_PROMPT}\nUser: {req.text}"
    resp = model.generate_content(prompt)
    parsed = extract_json(resp.text or "")

    parsed.setdefault("entities", {})
    if parsed.get("lang") not in ("en-US", "vi-VN"):
        parsed["lang"] = "vi-VN" if re.search(
            r"[ăâơưđêôàảãáạằắẳẵặầấẩẫậèéẻẽẹìíỉĩịòóỏõọồốổỗộờởỡớợùúủũụỳýỷỹỵ]",
            req.text, re.I
        ) else "en-US"

    if "intent" not in parsed:
        parsed = {"intent":"answer","answer": resp.text}
    if "entities" not in parsed:
        parsed["entities"] = {}
    print(parsed)
    return parsed

app = FastAPI()
app.include_router(router)


app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "https://nextune.ddnsgeek.com",
        "http://localhost:3000", 
        "http://localhost:5173",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# uvicorn app:app --host 0.0.0.0 --port 8081 --reload