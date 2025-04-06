package nfr.newsfeed.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import nfr.newsfeed.R;

public class MainNewsCardView extends ConstraintLayout {
    
    private ImageView newsImage;
    private TextView newsTitle;

    public MainNewsCardView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MainNewsCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainNewsCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.main_news_card, this, true);
        newsImage = findViewById(R.id.news_image);
        newsTitle = findViewById(R.id.news_title);
    }
    
    /**
     * Set the news card data
     * @param imageUrl URL of the news image
     * @param title News title text
     */
    public void setNewsData(String imageUrl, String title) {
        Glide.with(getContext()).load(imageUrl).placeholder(R.drawable.placeholder_loading).error(R.drawable.placeholder_error).into(newsImage);
        newsImage.setContentDescription(title);
        newsTitle.setText(title);
    }
    
    public ImageView getNewsImage() {
        return newsImage;
    }
    
    public TextView getNewsTitle() {
        return newsTitle;
    }
}