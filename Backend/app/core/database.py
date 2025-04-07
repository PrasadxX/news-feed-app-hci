from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase
from core.config import settings

class Database:
    client: AsyncIOMotorClient = None
    db: AsyncIOMotorDatabase = None

db = Database()

async def connect_to_mongo():
    """Connect to MongoDB."""
    db.client = AsyncIOMotorClient(settings.MONGODB_URL)
    db.db = db.client[settings.DATABASE_NAME]
    
    # Create indexes for better query performance
    await db.db.news.create_index("id", unique=True)
    await db.db.news.create_index("category_id")
    await db.db.news.create_index([("date", -1)])  # For sorting by date descending

async def close_mongo_connection():
    """Close MongoDB connection."""
    if db.client:
        db.client.close()