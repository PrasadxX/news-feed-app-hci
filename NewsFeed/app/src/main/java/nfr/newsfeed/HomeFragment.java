package nfr.newsfeed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import nfr.newsfeed.views.MainNewsCardView;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new NewsAdapter());

        MainNewsCardView newsCard = view.findViewById(R.id.main_news_card);
        newsCard.setNewsData("https://cdn.newsfirst.lk/sinhala-uploads/2025/04/New%20Project-587285_850x460.jpg", "Global Updates Latest Events");
        return view;
    }

    private static class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

        @NonNull
        @Override
        public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
            holder.textView.setText("News Article " + (position + 1));
        }

        @Override
        public int getItemCount() {
            return 10; // Example: 10 articles
        }

        static class NewsViewHolder extends RecyclerView.ViewHolder {
            MaterialTextView textView;

            NewsViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}