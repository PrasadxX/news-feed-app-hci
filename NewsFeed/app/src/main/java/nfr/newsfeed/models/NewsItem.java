package nfr.newsfeed.models;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsItem {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("short_title")
    private String shortTitle;

    @SerializedName("content")
    private String content;

    @SerializedName("excerpt")
    private String excerpt;

    @SerializedName("date")
    private String date;

    @SerializedName("post_url")
    private String postUrl;

    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("images")
    private Images images;

    @SerializedName("video_post")
    private Object videoPost;

    public NewsItem() {
        // Default constructor
    }

    public NewsItem(String id, String title, String time, int imageResId) {
        this.id = id;
        this.title = title;
        this.date = time;
        // This constructor is kept for compatibility with existing code
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getShortTitle() { return shortTitle; }
    public void setShortTitle(String shortTitle) { this.shortTitle = shortTitle; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPostUrl() { return postUrl; }
    public void setPostUrl(String postUrl) { this.postUrl = postUrl; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Images getImages() { return images; }
    public void setImages(Images images) { this.images = images; }

    public Object getVideoPost() { return videoPost; }
    public void setVideoPost(Object videoPost) { this.videoPost = videoPost; }

    // Legacy method for compatibility
    public String getTime() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm a", Locale.US);
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            return date;
        }
    }

    public int getImageResId() {
        // Legacy method, replaced with getImages()
        return 0;
    }

    // Helper method to get formatted relative time
    public String getFormattedTime() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDate = format.parse(date);
            Date now = new Date();

            long diff = now.getTime() - parsedDate.getTime();
            long minutes = diff / (60 * 1000);
            long hours = diff / (60 * 60 * 1000);
            long days = diff / (24 * 60 * 60 * 1000);

            if (days > 0) {
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            } else if (hours > 0) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else {
                return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
            }
        } catch (ParseException e) {
            return "Unknown time";
        }
    }

    // Inner class for images
    public static class Images {
        @SerializedName("news_detail_image")
        private String newsDetailImage;

        @SerializedName("post_thumb")
        private String postThumb;

        @SerializedName("mobile_banner")
        private String mobileBanner;

        @SerializedName("mini_tile_image")
        private String miniTileImage;

        @SerializedName("large_tile_image")
        private String largeTileImage;

        public String getNewsDetailImage() { return newsDetailImage; }
        public void setNewsDetailImage(String newsDetailImage) { this.newsDetailImage = newsDetailImage; }

        public String getPostThumb() { return postThumb; }
        public void setPostThumb(String postThumb) { this.postThumb = postThumb; }

        public String getMobileBanner() { return mobileBanner; }
        public void setMobileBanner(String mobileBanner) { this.mobileBanner = mobileBanner; }

        public String getMiniTileImage() { return miniTileImage; }
        public void setMiniTileImage(String miniTileImage) { this.miniTileImage = miniTileImage; }

        public String getLargeTileImage() { return largeTileImage; }
        public void setLargeTileImage(String largeTileImage) { this.largeTileImage = largeTileImage; }
    }
}