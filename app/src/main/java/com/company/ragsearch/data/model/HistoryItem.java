package com.company.ragsearch.data.model;

import com.google.gson.annotations.SerializedName;

// Элемент истории поиска
public class HistoryItem {

    @SerializedName("query")
    private String query;

    @SerializedName("answer")
    private String answer;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("best_instruction_title")
    private String bestInstructionTitle;

    public HistoryItem(String query, String answer, long timestamp, String bestInstructionTitle) {
        this.query = query;
        this.answer = answer;
        this.timestamp = timestamp;
        this.bestInstructionTitle = bestInstructionTitle;
    }

    // Getters
    public String getQuery() {
        return query;
    }

    public String getAnswer() {
        return answer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getBestInstructionTitle() {
        return bestInstructionTitle;
    }
}
