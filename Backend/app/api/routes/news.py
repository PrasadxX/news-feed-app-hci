from fastapi import APIRouter, HTTPException, Query, Depends
from typing import List

from models.news import NewsResponse, CategoryResponse, PaginatedNewsResponse
from services import news_service

router = APIRouter()

@router.get("/breaking-news", response_model=PaginatedNewsResponse)
async def get_breaking_news(
    page: int = Query(1, ge=1),
    limit: int = Query(10, ge=1, le=50)
):
    """
    Get breaking news with pagination.
    """
    skip = (page - 1) * limit
    
    # Get total count of news items
    total = await news_service.count_news_by_category(None)
    
    # Get news items
    news_items = await news_service.get_breaking_news(skip=skip, limit=limit)
    
    return PaginatedNewsResponse(
        total=total,
        page=page,
        limit=limit,
        news=[NewsResponse(**item) for item in news_items]
    )

@router.get("/category/{category_id}", response_model=PaginatedNewsResponse)
async def get_category_news(
    category_id: str,
    page: int = Query(1, ge=1),
    limit: int = Query(10, ge=1, le=50)
):
    """
    Get news by category with pagination.
    """
    skip = (page - 1) * limit
    
    # Get total count of news items in this category
    total = await news_service.count_news_by_category(category_id)
    
    # Get news items
    news_items = await news_service.get_category_news(
        category_id=category_id,
        skip=skip,
        limit=limit
    )
    
    return PaginatedNewsResponse(
        total=total,
        page=page,
        limit=limit,
        news=[NewsResponse(**item) for item in news_items]
    )

@router.get("/news/{news_id}", response_model=NewsResponse)
async def get_news_by_id(news_id: str):
    """
    Get a specific news item by ID.
    """
    news_item = await news_service.get_news_by_id(news_id)
    
    if not news_item:
        raise HTTPException(status_code=404, detail="News item not found")
    
    return NewsResponse(**news_item)

@router.get("/categories", response_model=List[CategoryResponse])
async def get_categories():
    """
    Get all categories.
    """
    categories = await news_service.get_categories()
    return [CategoryResponse(id=category.id, name=category.name) for category in categories]

@router.post("/admin/refresh-news", status_code=200)
async def refresh_news():
    """
    Manually trigger news fetch from original API.
    """
    await news_service.fetch_all_news()
    return {"message": "News refresh started"}