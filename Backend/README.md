# News Feed API

A FastAPI backend for the News Feed Android application, using MongoDB to store news data from an external source.

## Features

- Fetch and store news from external API
- Convert HTML content to Markdown
- Serve news data optimized for mobile consumption
- Category-based news filtering
- Background task for periodic news updates
- Docker Compose setup for easy deployment

## Setup and Installation

### Prerequisites

- Docker and Docker Compose
- Git

### Installation

1. Clone the repository:

```bash
git clone https://github.com/yourusername/news-feed-api.git
cd news-feed-api
```

2. Start the services with Docker Compose:

```bash
docker-compose up -d
```

The API will be available at http://localhost:8000

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/breaking-news` | GET | Get breaking news with pagination |
| `/api/category/{category_id}` | GET | Get news by category with pagination |
| `/api/news/{news_id}` | GET | Get a specific news item by ID |
| `/api/categories` | GET | Get list of all categories |
| `/api/admin/refresh-news` | POST | Manually trigger news fetch |

### Pagination

All list endpoints support pagination with the following query parameters:
- `page`: Page number (starting from 1)
- `limit`: Number of items per page (default 10, max 50)

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MONGODB_URL` | MongoDB connection URL | `mongodb://localhost:27017` |
| `DATABASE_NAME` | MongoDB database name | `news_feed` |
| `ORIGINAL_API_BASE_URL` | Original news API base URL | `https://apisinhala.newsfirst.lk` |
| `NEWS_FETCH_INTERVAL` | Interval (in seconds) for news fetch | `3600` (1 hour) |

## Development

To run the API locally without Docker:

1. Install dependencies:

```bash
pip install -r requirements.txt
```

2. Run the FastAPI application:

```bash
cd app
uvicorn main:app --reload
```

## Integration with Android App

This API is designed to work with the News Feed Android application. Here's how to integrate:

1. Update the API base URL in your Android app to point to this API
2. Use the appropriate endpoints for each screen:
   - Home screen: `/api/breaking-news` for main news and latest news
   - Category screen: `/api/categories` for category list and `/api/category/{id}` for category news
   - Settings screen: Keep application preferences client-side
