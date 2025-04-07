import os
from typing import Dict
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

class Settings:
    # API settings
    API_V1_PREFIX: str = "/api"
    PROJECT_NAME: str = "News Feed API"
    
    # MongoDB settings
    MONGODB_URL: str = os.getenv("MONGODB_URL", "mongodb://localhost:27017")
    DATABASE_NAME: str = os.getenv("DATABASE_NAME", "news_feed")
    
    # Original API settings
    ORIGINAL_API_BASE_URL: str = os.getenv("ORIGINAL_API_BASE_URL", "https://apisinhala.newsfirst.lk")
    
    # News fetch interval in seconds (default 1 hour)
    NEWS_FETCH_INTERVAL: int = int(os.getenv("NEWS_FETCH_INTERVAL", 3600))
    
    # Categories
    CATEGORIES: Dict[str, int] = {
        "Local": 81,
        "Sport": 83,
        "World": 84,
        "Business": 85,
        "Featured": 36569
    }
    
    # Endpoint mappings
    BREAKING_NEWS_ENDPOINT: str = "/post/PostPagination/{page}/{limit}/"
    CATEGORY_NEWS_ENDPOINT: str = "/post/categoryPostPagination/{category_id}/{page}/{limit}/"
    
    # News fetch batch size
    BREAKING_NEWS_LIMIT: int = 10
    CATEGORY_NEWS_LIMIT: int = 20

settings = Settings()