package nfr.newsfeed.api;

import java.util.List;

import nfr.newsfeed.models.Category;
import nfr.newsfeed.models.NewsItem;
import nfr.newsfeed.models.PaginatedResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApiService {

    @GET("api/breaking-news")
    Call<PaginatedResponse<NewsItem>> getBreakingNews(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/category/{categoryId}")
    Call<PaginatedResponse<NewsItem>> getCategoryNews(
            @Path("categoryId") int categoryId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/news/{newsId}")
    Call<NewsItem> getNewsById(
            @Path("newsId") String newsId
    );

    @GET("api/categories")
    Call<List<Category>> getCategories();
}