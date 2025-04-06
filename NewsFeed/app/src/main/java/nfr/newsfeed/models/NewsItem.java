package nfr.newsfeed.models;

public class NewsItem {
    private String id;
    private String title;
    private String time;
    private int imageResId; // Or String if using URL

    public NewsItem(String id, String title, String time, int imageResId) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.imageResId = imageResId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getTime() { return time; }
    public int getImageResId() { return imageResId; }
}
