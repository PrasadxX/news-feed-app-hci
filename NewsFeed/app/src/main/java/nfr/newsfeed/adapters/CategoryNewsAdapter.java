package nfr.newsfeed.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

import java.util.List;

import nfr.newsfeed.R;
import nfr.newsfeed.activities.CategoryNewsActivity;
import nfr.newsfeed.models.NewsItem;

public class CategoryNewsAdapter extends RecyclerView.Adapter<CategoryNewsAdapter.CategoryNewsViewHolder> {

    private Context context;
    private List<NewsItem> newsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public CategoryNewsAdapter(Context context, List<NewsItem> newsList, OnItemClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_card, parent, false);
        return new CategoryNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryNewsViewHolder holder, int position) {
        NewsItem item = newsList.get(position);

        // Set title and description
        holder.titleText.setText(item.getTitle());
        holder.descriptionText.setText(android.text.Html.fromHtml(item.getExcerpt(), android.text.Html.FROM_HTML_MODE_COMPACT).toString());

        // Set category chip
        holder.categoryChip.setText(item.getCategoryName());

        holder.categoryChip.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryNewsActivity.class);
            intent.putExtra("category_id", Integer.parseInt(item.getCategoryId()));
            intent.putExtra("category_name", item.getCategoryName());
            startActivity(context,intent,null);
        });

        // Set time
        holder.timeText.setText(item.getFormattedTime());

        // Load image with Glide
        if (item.getImages() != null && item.getImages().getNewsDetailImage() != null) {
            Glide.with(context)
                    .load(item.getImages().getNewsDetailImage())
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(holder.newsImage);
        } else {
            holder.newsImage.setImageResource(R.drawable.placeholder_error);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public void updateData(List<NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public static class CategoryNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;
        TextView titleText;
        TextView descriptionText;
        TextView timeText;
        Chip categoryChip;

        public CategoryNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            timeText = itemView.findViewById(R.id.timeText);
            categoryChip = itemView.findViewById(R.id.categoryChip);
        }
    }
}