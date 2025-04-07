import logging
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from apscheduler.schedulers.asyncio import AsyncIOScheduler

from core.config import settings
from core.database import connect_to_mongo, close_mongo_connection
from api.routes import news
from services.news_service import fetch_all_news

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)

# Initialize FastAPI app
app = FastAPI(
    title=settings.PROJECT_NAME,
    openapi_url=f"{settings.API_V1_PREFIX}/openapi.json",
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allow all origins (you should restrict this in production)
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(news.router, prefix=f"{settings.API_V1_PREFIX}", tags=["news"])

# Create scheduler
scheduler = AsyncIOScheduler()

@app.on_event("startup")
async def startup_event():
    logger.info("Connecting to MongoDB...")
    await connect_to_mongo()
    
    logger.info("Starting scheduler...")
    # Schedule news fetch job
    scheduler.add_job(
        fetch_all_news,
        trigger="interval",
        seconds=settings.NEWS_FETCH_INTERVAL,
        id="fetch_news_job",
        replace_existing=True,
    )
    scheduler.start()
    
    # Initial news fetch
    logger.info("Initial news fetch...")
    await fetch_all_news()

@app.on_event("shutdown")
async def shutdown_event():
    logger.info("Shutting down scheduler...")
    scheduler.shutdown()
    
    logger.info("Closing MongoDB connection...")
    await close_mongo_connection()

@app.get("/")
async def root():
    return {
        "message": "News Feed API",
        "docs": "/docs",
    }

@app.get("/health")
async def health_check():
    return {"status": "healthy"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)