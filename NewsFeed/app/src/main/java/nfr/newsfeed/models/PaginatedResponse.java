package nfr.newsfeed.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaginatedResponse<T> {
    @SerializedName("total")
    private int total;

    @SerializedName("page")
    private int page;

    @SerializedName("limit")
    private int limit;

    @SerializedName("news")
    private List<T> news;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<T> getNews() {
        return news;
    }

    public void setNews(List<T> news) {
        this.news = news;
    }
}