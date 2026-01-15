package com.company.ragsearch.data.api;

import com.company.ragsearch.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static ApiClient instance;
    private Retrofit retrofit;
    private BasicAuthInterceptor authInterceptor;
    private RagApiService apiService;

    private ApiClient() {

    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public void initialize(String username, String password) {
        authInterceptor = new BasicAuthInterceptor(username, password);

        // OkHttp клиент
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Создаем API сервис
        apiService = retrofit.create(RagApiService.class);
    }

    // Получить API сервис
    public RagApiService getApiService() {
        if (apiService == null) {
            throw new IllegalStateException("ApiClient не инициализирован. Вызовите initialize() сначала.");
        }
        return apiService;
    }

    // Сброс клиента
    public void reset() {
        retrofit = null;
        authInterceptor = null;
        apiService = null;
    }
}
