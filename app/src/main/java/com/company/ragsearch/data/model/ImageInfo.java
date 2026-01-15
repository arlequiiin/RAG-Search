package com.company.ragsearch.data.model;

import com.google.gson.annotations.SerializedName;

public class ImageInfo {

    @SerializedName("path")
    private String path;

    @SerializedName("url")
    private String url;

    @SerializedName("base64")
    private String base64;

    public ImageInfo() {
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public String getBase64() {
        return base64;
    }

    public boolean hasBase64() {
        return base64 != null && !base64.isEmpty();
    }
}
