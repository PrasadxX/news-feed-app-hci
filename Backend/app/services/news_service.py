import httpx
import asyncio
import logging
from datetime import datetime
from typing import List, Dict, Any, Optional
from motor.motor_asyncio import AsyncIOMotorCollection
from pymongo.errors import DuplicateKeyError

from core.config import settings
from core.database import db
from utils.html_converter import html_to_markdown
from models.news import NewsInDB, CategoryBase

logger = logging.getLogger(__name__)

async def fetch_original_api(endpoint: str) -> Dict[str, Any]:
    """
    Fetch data from original API.
    """
    url = f"{settings.ORIGINAL_API_BASE_URL}{endpoint}"
    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(url, timeout=30.0)
            response.raise_for_status()
            return response.json()
        except httpx.HTTPError as e:
            logger.error(f"HTTP error occurred when fetching from {url}: {e}")
            return {}
        except Exception as e:
            logger.error(f"Error occurred when fetching from {url}: {e}")
            return {}

async def process_news_item(news_item: Dict[str, Any], category_id: Optional[str] = None, category_name: Optional[str] = None) -> NewsInDB:
    """
    Process a news item from the original API and convert to our model.
    """
    # Extract required fields
    id_str = news_item.get('id', '')
    
    # Handle nested title
    title = news_item.get('title', {}).get('rendered', '')
    if not title and isinstance(news_item.get('title'), str):
        title = news_item.get('title', '')
    
    # Convert HTML content to markdown
    content_html = news_item.get('content', {}).get('rendered', '')
    excerpt_html = news_item.get('excerpt', {}).get('rendered', '')
    
    content_markdown = html_to_markdown(content_html)
    excerpt_markdown = html_to_markdown(excerpt_html)
    
    # Parse date
    date_str = news_item.get('date_gmt') 
        
    try:
        if date_str:
            if date_str.endswith('Z'):
                # Handle ISO format with Z timezone indicator
                date_obj = datetime.strptime(date_str, '%Y-%m-%dT%H:%M:%S.%fZ')
            else:
                try:
                    # Try parsing other common formats
                    date_obj = datetime.strptime(date_str, '%d-%m-%YT%I:%M %p')
                except ValueError:
                    # Default to current time if parsing fails
                    date_obj = datetime.now()
        else:
            date_obj = datetime.now()
    except Exception as e:
        date_obj = datetime.now()
        
    # Create NewsInDB object
    news_db = NewsInDB(
        id=id_str,
        title=title,
        short_title=news_item.get('short_title', ''),
        content=content_markdown,
        excerpt=excerpt_markdown,
        date=date_obj,
        post_url=news_item.get('post_url', ''),
        category_id=category_id,
        category_name=category_name,
        images=news_item.get('images', {}),
        video_post=news_item.get('video_post', None),
        fetch_date=datetime.now()
    )
    
    return news_db

async def fetch_and_store_breaking_news():
    """
    Fetch breaking news and store in database.
    """
    logger.info("Fetching breaking news")
    page = 0
    limit = settings.BREAKING_NEWS_LIMIT
    
    endpoint = settings.BREAKING_NEWS_ENDPOINT.format(page=page, limit=limit)
    data = await fetch_original_api(endpoint)
    
    if not data or 'postResponseDto' not in data:
        logger.error("Failed to fetch breaking news or invalid response format")
        return
    
    news_collection: AsyncIOMotorCollection = db.db.news
    
    for item in data.get('postResponseDto', []):
        try:
            news_item = await process_news_item(item)
            
            # Upsert to database (update if exists, insert if not)
            await news_collection.update_one(
                {"id": news_item.id},
                {"$set": news_item.model_dump()},
                upsert=True
            )
        except Exception as e:
            logger.error(f"Error processing breaking news item: {e}")

async def fetch_and_store_category_news():
    """
    Fetch news for each category and store in database.
    """
    logger.info("Fetching category news")
    page = 0
    limit = settings.CATEGORY_NEWS_LIMIT
    
    for category_name, category_id in settings.CATEGORIES.items():
        logger.info(f"Fetching news for category: {category_name}")
        
        endpoint = settings.CATEGORY_NEWS_ENDPOINT.format(
            category_id=category_id,
            page=page,
            limit=limit
        )
        
        data = await fetch_original_api(endpoint)
        
        if not data or 'postResponseDto' not in data:
            logger.error(f"Failed to fetch news for category {category_name} or invalid response format")
            continue
        
        news_collection: AsyncIOMotorCollection = db.db.news
        
        for item in data.get('postResponseDto', []):
            try:
                news_item = await process_news_item(
                    item, 
                    category_id=str(category_id),
                    category_name=category_name
                )
                
                # Upsert to database
                await news_collection.update_one(
                    {"id": news_item.id},
                    {"$set": news_item.model_dump()},
                    upsert=True
                )
            except Exception as e:
                logger.error(f"Error processing category news item: {e}")

async def fetch_all_news():
    """
    Fetch both breaking news and category news.
    """
    await fetch_and_store_breaking_news()
    await fetch_and_store_category_news()

async def get_breaking_news(skip: int = 0, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Get breaking news from database.
    """
    news_collection: AsyncIOMotorCollection = db.db.news
    
    # No specific category filter for breaking news
    cursor = news_collection.find({}).sort("date", -1).skip(skip).limit(limit)
    
    news_list = []
    async for news in cursor:
        news_list.append(news)
    
    return news_list

async def get_category_news(category_id: str, skip: int = 0, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Get news by category from database.
    """
    news_collection: AsyncIOMotorCollection = db.db.news
    
    cursor = news_collection.find(
        {"category_id": category_id}
    ).sort("date", -1).skip(skip).limit(limit)
    
    news_list = []
    async for news in cursor:
        news_list.append(news)
    
    return news_list

async def get_news_by_id(news_id: str) -> Optional[Dict[str, Any]]:
    """
    Get a specific news item by ID.
    """
    news_collection: AsyncIOMotorCollection = db.db.news
    
    news = await news_collection.find_one({"id": news_id})
    return news

async def get_categories() -> List[CategoryBase]:
    """
    Get all categories.
    """
    return [
        CategoryBase(id=category_id, name=category_name)
        for category_name, category_id in settings.CATEGORIES.items()
    ]

async def count_news_by_category(category_id: Optional[str] = None) -> int:
    """
    Count news items, optionally filtered by category.
    """
    news_collection: AsyncIOMotorCollection = db.db.news
    
    filter_dict = {}
    if category_id:
        filter_dict["category_id"] = category_id
    
    return await news_collection.count_documents(filter_dict)
