package nfr.newsfeed.api;

import java.util.List;

import nfr.newsfeed.models.ApiResponse;
import nfr.newsfeed.models.Category;
import nfr.newsfeed.models.NewsItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApiService {

    @GET("post/PostPagination/{page}/{limit}")
    Call<ApiResponse> getBreakingNews(
            @Path("page") int page,
            @Path("limit") int limit
    );

    @GET("post/categoryPostPagination/{categoryId}/{page}/{limit}")
    Call<ApiResponse> getCategoryNews(
            @Path("categoryId") int categoryId,
            @Path("page") int page,
            @Path("limit") int limit
    );

    @GET("Categories")
    Call<List<Category>> getCategories();
}