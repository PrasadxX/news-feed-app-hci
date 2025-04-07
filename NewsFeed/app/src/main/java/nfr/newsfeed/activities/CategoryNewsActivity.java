package nfr.newsfeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import nfr.newsfeed.R;
import nfr.newsfeed.adapters.CategoryNewsAdapter;
import nfr.newsfeed.api.NewsApiService;
import nfr.newsfeed.api.RetrofitClient;
import nfr.newsfeed.models.ApiResponse;
import nfr.newsfeed.models.NewsItem;
import nfr.newsfeed.models.PaginatedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryNewsActivity extends AppCompatActivity implements CategoryNewsAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private CategoryNewsAdapter adapter;
    private ShimmerFrameLayout shimmerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<NewsItem> newsList = new ArrayList<>();

    private int categoryId;
    private String categoryName;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int ITEMS_PER_PAGE = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_news);

        // Get category ID and name from intent
        categoryId = getIntent().getIntExtra("category_id", 0);
        categoryName = getIntent().getStringExtra("category_name");

        if (categoryId == 0) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoryName);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view);
        shimmerLayout = findViewById(R.id.shimmer_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CategoryNewsAdapter(this, newsList, this);
        recyclerView.setAdapter(adapter);

        // Setup pagination
        setupPagination(layoutManager);

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshNews);

        // Load news
        loadCategoryNews(currentPage, false);
    }

    private void setupPagination(LinearLayoutManager layoutManager) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= ITEMS_PER_PAGE) {
                        loadMoreItems();
                    }
                }
            }
        });
    }

    private void loadMoreItems() {
        isLoading = true;
        currentPage++;
        loadCategoryNews(currentPage, true);
    }

    private void refreshNews() {
        currentPage = 1;
        isLastPage = false;
        newsList.clear();
        adapter.notifyDataSetChanged();
        loadCategoryNews(currentPage, false);
    }

    private void loadCategoryNews(int page, boolean isLoadMore) {
        if (!isLoadMore) {
            startShimmerEffect();
        }

        NewsApiService apiService = RetrofitClient.getClient().create(NewsApiService.class);
        apiService.getCategoryNews(categoryId, page, ITEMS_PER_PAGE).enqueue(new Callback<PaginatedResponse<NewsItem>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<NewsItem>> call, Response<PaginatedResponse<NewsItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> items = response.body().getNews();

                    if (items == null || items.isEmpty()) {
                        isLastPage = true;
                    } else {
                        newsList.addAll(items);
                        adapter.notifyDataSetChanged();
                    }
                }

                isLoading = false;
                stopShimmerEffect();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<PaginatedResponse<NewsItem>> call, Throwable t) {
                Toast.makeText(CategoryNewsActivity.this, "Failed to load news: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isLoading = false;
                stopShimmerEffect();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(NewsItem newsItem) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("news_id", newsItem.getId());
        startActivity(intent);
    }

    private void startShimmerEffect() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
    }

    private void stopShimmerEffect() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}