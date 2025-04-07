package nfr.newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import nfr.newsfeed.activities.CategoryNewsActivity;
import nfr.newsfeed.adapters.CategoryAdapter;
import nfr.newsfeed.api.NewsApiService;
import nfr.newsfeed.api.RetrofitClient;
import nfr.newsfeed.models.Category;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ShimmerFrameLayout shimmerLayout;
    private List<Category> categories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        shimmerLayout = view.findViewById(R.id.shimmer_layout);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new CategoryAdapter(requireContext(), categories, this);
        recyclerView.setAdapter(adapter);

        // Load categories
        loadCategories();

        return view;
    }

    private void loadCategories() {
        startShimmerEffect();

        NewsApiService apiService = RetrofitClient.getClient().create(NewsApiService.class);
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
                stopShimmerEffect();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load categories: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                stopShimmerEffect();
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        // Navigate to category news screen
        Intent intent = new Intent(requireContext(), CategoryNewsActivity.class);
        intent.putExtra("category_id", category.getId());
        intent.putExtra("category_name", category.getName());
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
}