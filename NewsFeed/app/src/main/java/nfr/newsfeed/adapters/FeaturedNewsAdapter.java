package nfr.newsfeed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nfr.newsfeed.R;
import nfr.newsfeed.models.NewsItem;

public class FeaturedNewsAdapter extends RecyclerView.Adapter<FeaturedNewsAdapter.FeaturedNewsViewHolder> {

    private List<NewsItem> newsList;

    public FeaturedNewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public FeaturedNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_news_card, parent, false);
        return new FeaturedNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedNewsViewHolder holder, int position) {
        NewsItem item = newsList.get(position);
        holder.title.setText(item.getTitle());
        holder.time.setText(item.getTime());
        holder.image.setImageResource(item.getImageResId()); // or load with Glide if it's a URL
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class FeaturedNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView time;
        TextView title;

        public FeaturedNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.news_image);
            time = itemView.findViewById(R.id.time_chip);
            title = itemView.findViewById(R.id.mews_title);
        }
    }
}
