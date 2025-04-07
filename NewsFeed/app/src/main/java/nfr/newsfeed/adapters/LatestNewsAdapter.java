package nfr.newsfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import nfr.newsfeed.R;
import nfr.newsfeed.models.NewsItem;

public class LatestNewsAdapter extends RecyclerView.Adapter<LatestNewsAdapter.LatestNewsViewHolder> {

    private Context context;
    private List<NewsItem> newsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public LatestNewsAdapter(Context context, List<NewsItem> newsList, OnItemClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LatestNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.featured_news_card, parent, false);
        return new LatestNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LatestNewsViewHolder holder, int position) {
        NewsItem item = newsList.get(position);

        // Set title
        holder.title.setText(item.getShortTitle() != null ? item.getShortTitle() : item.getTitle());

        // Set time
        holder.time.setText(item.getFormattedTime());

        // Load image with Glide
        if (item.getImages() != null && item.getImages().getMiniTileImage() != null) {
            Glide.with(context)
                    .load(item.getImages().getMiniTileImage())
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.placeholder_error);
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

    public static class LatestNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView time;
        TextView title;

        public LatestNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.news_image);
            time = itemView.findViewById(R.id.time_chip);
            title = itemView.findViewById(R.id.mews_title);
        }
    }
}