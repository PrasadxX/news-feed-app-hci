package nfr.newsfeed.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("rowCount")
    private String rowCount;

    @SerializedName("postResponseDto")
    private List<NewsItem> postResponseDto;

    public String getRowCount() {
        return rowCount;
    }

    public void setRowCount(String rowCount) {
        this.rowCount = rowCount;
    }

    public List<NewsItem> getPostResponseDto() {
        return postResponseDto;
    }

    public void setPostResponseDto(List<NewsItem> postResponseDto) {
        this.postResponseDto = postResponseDto;
    }
}