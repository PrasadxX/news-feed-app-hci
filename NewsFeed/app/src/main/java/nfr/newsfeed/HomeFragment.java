package nfr.newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import nfr.newsfeed.activities.NewsDetailActivity;
import nfr.newsfeed.adapters.CategoryNewsAdapter;
import nfr.newsfeed.adapters.LatestNewsAdapter;
import nfr.newsfeed.api.NewsApiService;
import nfr.newsfeed.api.RetrofitClient;
import nfr.newsfeed.models.ApiResponse;
import nfr.newsfeed.models.NewsItem;
import nfr.newsfeed.models.PaginatedResponse;
import nfr.newsfeed.utils.PreferencesManager;
import nfr.newsfeed.views.MainNewsCardView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private MainNewsCardView mainNewsCard;
    private RecyclerView latestNewsRecyclerView;
    private RecyclerView categoryNewsRecyclerView;
    private ShimmerFrameLayout shimmerLayout;

    private LatestNewsAdapter latestNewsAdapter;
    private CategoryNewsAdapter categoryNewsAdapter;

    private NewsApiService newsApiService;
    private PreferencesManager preferencesManager;

    private List<NewsItem> latestNewsList = new ArrayList<>();
    private List<NewsItem> categoryNewsList = new ArrayList<>();

    private NewsItem mainNewsItem;
    private Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;
    private int currentCarouselPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        mainNewsCard = view.findViewById(R.id.main_news_card);
        latestNewsRecyclerView = view.findViewById(R.id.featuredNewsRecyclerView);
        categoryNewsRecyclerView = view.findViewById(R.id.recycler_view);
        shimmerLayout = view.findViewById(R.id.shimmer_layout);

        // Initialize API service and preferences
        newsApiService = RetrofitClient.getClient().create(NewsApiService.class);
        preferencesManager = new PreferencesManager(requireContext());

        // Setup adapters
        setupLatestNewsRecyclerView();
        setupCategoryNewsRecyclerView();

        // Start loading data
        startShimmerEffect();
        loadMainNewsAndLatestNews();
        loadCategoryNews(preferencesManager.getSelectedCategoryId());

        return view;
    }

    private void setupLatestNewsRecyclerView() {
        // Use horizontal layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false);
        latestNewsRecyclerView.setLayoutManager(layoutManager);

        // Create adapter
        latestNewsAdapter = new LatestNewsAdapter(
                requireContext(),
                latestNewsList,
                this::openNewsDetail
        );
        latestNewsRecyclerView.setAdapter(latestNewsAdapter);

        // Add snap helper for carousel effect
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(latestNewsRecyclerView);

        // Setup auto-scroll for carousel
        setupCarouselAutoScroll();

        // Add scroll listener to update current position
        latestNewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = layoutManager.findFirstVisibleItemPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        currentCarouselPosition = position;
                    }
                }
            }
        });
    }

    private void setupCategoryNewsRecyclerView() {
        categoryNewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryNewsAdapter = new CategoryNewsAdapter(
                requireContext(),
                categoryNewsList,
                this::openNewsDetail
        );
        categoryNewsRecyclerView.setAdapter(categoryNewsAdapter);
    }

    private void setupCarouselAutoScroll() {
        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                if (latestNewsList.size() > 0) {
                    currentCarouselPosition++;
                    if (currentCarouselPosition >= latestNewsList.size()) {
                        currentCarouselPosition = 0;
                    }
                    latestNewsRecyclerView.smoothScrollToPosition(currentCarouselPosition);
                }
                carouselHandler.postDelayed(this, 5000); // Scroll every 5 seconds
            }
        };
    }

    private void startCarouselAutoScroll() {
        carouselHandler.postDelayed(carouselRunnable, 5000); // Start after 5 seconds
    }

    private void stopCarouselAutoScroll() {
        carouselHandler.removeCallbacks(carouselRunnable);
    }

    private void loadMainNewsAndLatestNews() {
        newsApiService.getBreakingNews(1, 10).enqueue(new Callback<PaginatedResponse<NewsItem>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<NewsItem>> call, Response<PaginatedResponse<NewsItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> breakingNews = response.body().getNews();

                    if (breakingNews != null && !breakingNews.isEmpty()) {
                        // Set main news card
                        mainNewsItem = breakingNews.get(0);
                        if (mainNewsItem.getImages() != null) {
                            mainNewsCard.setNewsData(
                                    mainNewsItem.getImages().getLargeTileImage(),
                                    mainNewsItem.getTitle()
                            );

                            // Set click listener for main news card
                            mainNewsCard.setOnClickListener(v -> openNewsDetail(mainNewsItem));
                        }

                        // Set latest news carousel (excluding main news)
                        latestNewsList.clear();
                        for (int i = 1; i < breakingNews.size(); i++) {
                            latestNewsList.add(breakingNews.get(i));
                        }
                        latestNewsAdapter.notifyDataSetChanged();

                        // Start auto-scrolling carousel
                        startCarouselAutoScroll();
                    }

                    stopShimmerEffect();
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<NewsItem>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load breaking news: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                stopShimmerEffect();
            }
        });
    }

    private void loadCategoryNews(int categoryId) {
        newsApiService.getCategoryNews(categoryId, 1, 20).enqueue(new Callback<PaginatedResponse<NewsItem>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<NewsItem>> call, Response<PaginatedResponse<NewsItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> categoryNews = response.body().getNews();

                    if (categoryNews != null) {
                        categoryNewsList.clear();
                        categoryNewsList.addAll(categoryNews);
                        categoryNewsAdapter.notifyDataSetChanged();
                    }

                    stopShimmerEffect();
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<NewsItem>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load category news: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                stopShimmerEffect();
            }
        });
    }

    private void openNewsDetail(NewsItem newsItem) {
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra("news_id", newsItem.getId());
        startActivity(intent);
    }

    private void startShimmerEffect() {
        if (shimmerLayout != null) {
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
        }
    }

    private void stopShimmerEffect() {
        if (shimmerLayout != null) {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCarouselAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCarouselAutoScroll();
    }
}