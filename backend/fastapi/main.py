from fastapi import FastAPI

app = FastAPI("InsightX FastAPI Intelligence API")

@app.get("/health")
async def health_check():
    return {"status": "ok"}