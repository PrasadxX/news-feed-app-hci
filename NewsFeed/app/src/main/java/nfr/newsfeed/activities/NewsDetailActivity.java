package nfr.newsfeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;

import io.noties.markwon.Markwon;
import nfr.newsfeed.R;
import nfr.newsfeed.api.NewsApiService;
import nfr.newsfeed.api.RetrofitClient;
import nfr.newsfeed.models.ApiResponse;
import nfr.newsfeed.models.NewsItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView newsImage;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView contentTextView;
    private Chip categoryChip;
    private ShimmerFrameLayout shimmerLayout;
    private Markwon markwon;
    private NestedScrollView scrollView;

    private String newsId;
    private int scrollPosition = 0;
    private static final String SCROLL_POSITION_KEY = "scroll_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Initialize Views
        newsImage = findViewById(R.id.newsImage);
        titleTextView = findViewById(R.id.titleTextView);
        dateTextView = findViewById(R.id.dateTextView);
        contentTextView = findViewById(R.id.contentTextView);
        categoryChip = findViewById(R.id.categoryChip);
        shimmerLayout = findViewById(R.id.shimmerLayout);
        
        // Fix: Update to match the actual ID in the layout
        scrollView = findViewById(R.id.nested_scroll_view);
        
        // Debug log to verify if we found the view
        if (scrollView == null) {
            Log.e("NewsDetailActivity", "NestedScrollView not found. Check if ID is correct in layout.");
        }

        // Initialize Markwon
        markwon = Markwon.create(this);

        // Get news ID from intent
        newsId = getIntent().getStringExtra("news_id");
        if (newsId == null) {
            Toast.makeText(this, "News ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Restore scroll position if available
        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY, 0);
        }

        // Load news details
        loadNewsDetails(newsId);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current scroll position when the activity state is saved
        if (scrollView != null) {
            outState.putInt(SCROLL_POSITION_KEY, scrollView.getScrollY());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save current scroll position when the activity is paused
        if (scrollView != null) {
            scrollPosition = scrollView.getScrollY();
            getPreferences(MODE_PRIVATE).edit()
                    .putInt(newsId + "_scroll_position", scrollPosition)
                    .apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore scroll position when returning to this activity
        if (scrollView != null && newsId != null) {
            scrollPosition = getPreferences(MODE_PRIVATE)
                    .getInt(newsId + "_scroll_position", 0);
            scrollView.post(() -> scrollView.scrollTo(0, scrollPosition));
        }
    }

    private void loadNewsDetails(String newsId) {
        startShimmerEffect();

        // Use the dedicated endpoint to get a single news item
        NewsApiService apiService = RetrofitClient.getClient().create(NewsApiService.class);
        apiService.getNewsById(newsId).enqueue(new Callback<NewsItem>() {
            @Override
            public void onResponse(Call<NewsItem> call, Response<NewsItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsItem newsItem = response.body();
                    displayNewsDetails(newsItem);
                    stopShimmerEffect();
                } else {
                    // News item not found
                    Toast.makeText(NewsDetailActivity.this, "News not found", Toast.LENGTH_SHORT).show();
                    stopShimmerEffect();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<NewsItem> call, Throwable t) {
                Toast.makeText(NewsDetailActivity.this, "Failed to load news: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                stopShimmerEffect();
                finish();
            }
        });
    }

    private void displayNewsDetails(NewsItem newsItem) {
        // Set title
        titleTextView.setText(newsItem.getTitle());

        // Set date
        dateTextView.setText(newsItem.getFormattedTime());

        // Set category and make it clickable
        categoryChip.setText(newsItem.getCategoryName());
        
        // Set up click listener for category chip
        categoryChip.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryNewsActivity.class);
            intent.putExtra("category_id", Integer.parseInt(newsItem.getCategoryId()));
            intent.putExtra("category_name", newsItem.getCategoryName());
            startActivity(intent);
        });

        // Load image with Glide
        if (newsItem.getImages() != null && newsItem.getImages().getNewsDetailImage() != null) {
            Glide.with(this)
                    .load(newsItem.getImages().getNewsDetailImage())
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(newsImage);
        } else {
            newsImage.setImageResource(R.drawable.placeholder_error);
        }

        // Convert HTML content to Markdown and render
        String htmlContent = newsItem.getContent();
        // For now, just using HTML directly until we add proper HTML to Markdown conversion
        contentTextView.setText(android.text.Html.fromHtml(htmlContent, android.text.Html.FROM_HTML_MODE_COMPACT));
        
        // Add null check before using scrollView
        if (scrollView != null) {
            scrollView.post(() -> scrollView.scrollTo(0, scrollPosition));
        }
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
            // Save scroll position before going back
            if (scrollView != null) {
                scrollPosition = scrollView.getScrollY();
                getPreferences(MODE_PRIVATE).edit()
                        .putInt(newsId + "_scroll_position", scrollPosition)
                        .apply();
            }
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Make sure we save scroll position before navigating back
        if (scrollView != null && newsId != null) {
            scrollPosition = scrollView.getScrollY();
            getPreferences(MODE_PRIVATE).edit()
                    .putInt(newsId + "_scroll_position", scrollPosition)
                    .apply();
        }
        
        super.onBackPressed();
    }
}