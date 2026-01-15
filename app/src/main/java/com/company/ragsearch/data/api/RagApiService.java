package com.company.ragsearch.data.api;

import com.company.ragsearch.data.model.HealthResponse;
import com.company.ragsearch.data.model.SearchRequest;
import com.company.ragsearch.data.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

// API интерфейс для взаимодействия с сервером
public interface RagApiService {

    // Проверка здоровья сервера и статуса RAG pipeline
    @GET("/api/health")
    Call<HealthResponse> checkHealth();

    // Отправка поискового запроса
    @POST("/api/search")
    Call<SearchResponse> search(@Body SearchRequest request);
}
