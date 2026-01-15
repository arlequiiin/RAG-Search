package com.company.ragsearch.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {

    @SerializedName("answer")
    private String answer;

    @SerializedName("images")
    private List<ImageInfo> images;

    @SerializedName("query")
    private String query;

    @SerializedName("timestamp")
    private String timestamp;

    public SearchResponse() {
    }

    public String getAnswer() {
        return answer;
    }

    public List<ImageInfo> getImages() {
        return images;
    }

    public String getQuery() {
        return query;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    public int getImagesCount() {
        return images != null ? images.size() : 0;
    }
}
