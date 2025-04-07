package nfr.newsfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nfr.newsfeed.R;
import nfr.newsfeed.models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private OnCategoryClickListener listener;
    private Map<String, Integer> categoryImages;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
        setupCategoryImages();
    }

    private void setupCategoryImages() {
        categoryImages = new HashMap<>();
        categoryImages.put("Local", R.drawable.category_local);
        categoryImages.put("Sport", R.drawable.category_sports);
        categoryImages.put("World", R.drawable.category_world);
        categoryImages.put("Business", R.drawable.category_business);
        categoryImages.put("Featured", R.drawable.category_featured);
        // Add more categories as needed
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        // Set category name
        holder.categoryName.setText(category.getName());

        // Set category image
        Integer imageResId = categoryImages.get(category.getName());
        if (imageResId != null) {
            holder.categoryImage.setImageResource(imageResId);
        } else {
            // Default image if category not found in map
            holder.categoryImage.setImageResource(R.drawable.placeholder_error);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void updateData(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}