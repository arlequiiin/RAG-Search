package com.company.ragsearch.data.model;

import com.google.gson.annotations.SerializedName;

public class SearchRequest {

    @SerializedName("query")
    private String query;

    @SerializedName("top_k")
    private Integer topK;

    public SearchRequest(String query) {
        this.query = query;
        this.topK = 5; // default value
    }

    public SearchRequest(String query, int topK) {
        this.query = query;
        this.topK = topK;
    }

    public String getQuery() {
        return query;
    }

    public Integer getTopK() {
        return topK;
    }
}
