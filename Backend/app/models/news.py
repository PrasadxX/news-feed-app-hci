from typing import Dict, Optional, Any, List
from pydantic import BaseModel, Field
from datetime import datetime

class NewsImage(BaseModel):
    news_detail_image: Optional[str] = None
    post_thumb: Optional[str] = None
    mobile_banner: Optional[str] = None
    mini_tile_image: Optional[str] = None
    large_tile_image: Optional[str] = None

class VideoPost(BaseModel):
    url: Optional[str] = None
    thumbnail: Optional[str] = None

class NewsBase(BaseModel):
    id: str
    title: str
    short_title: Optional[str] = None
    content: str
    excerpt: str
    date: datetime
    post_url: Optional[str] = None
    category_id: Optional[str] = None
    category_name: Optional[str] = None
    images: Optional[NewsImage] = None
    video_post: Optional[VideoPost] = None

class NewsInDB(NewsBase):
    fetch_date: datetime = Field(default_factory=datetime.now)

    class Config:
        populate_by_name = True

class NewsResponse(NewsBase):
    class Config:
        json_encoders = {
            datetime: lambda v: v.strftime("%Y-%m-%dT%H:%M:%S.%fZ")
        }

class CategoryBase(BaseModel):
    id: int
    name: str

class CategoryResponse(CategoryBase):
    pass

class PaginatedNewsResponse(BaseModel):
    total: int
    page: int
    limit: int
    news: List[NewsResponse]