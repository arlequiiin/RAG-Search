package com.company.ragsearch.data.model;

import com.google.gson.annotations.SerializedName;

public class HealthResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("version")
    private String version;

    @SerializedName("rag_initialized")
    private boolean ragInitialized;

    @SerializedName("timestamp")
    private String timestamp;

    public HealthResponse() {
    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    public boolean isRagInitialized() {
        return ragInitialized;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isHealthy() {
        return "healthy".equalsIgnoreCase(status) && ragInitialized;
    }
}
