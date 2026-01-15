package com.company.ragsearch.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.company.ragsearch.R;
import com.company.ragsearch.data.api.ApiClient;
import com.company.ragsearch.data.api.RagApiService;
import com.company.ragsearch.data.model.HealthResponse;
import com.company.ragsearch.ui.main.MainActivity;
import com.company.ragsearch.utils.PreferencesManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Экран авторизации в приложении
public class LoginActivity extends AppCompatActivity {

    // UI элементы
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private TextView tvError;

    // Менеджер настроек
    private PreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация менеджера настроек
        prefsManager = PreferencesManager.getInstance(this);

        // Инициализация UI элементов
        initViews();

        // Проверка сохраненных credentials
        checkSavedCredentials();

        // Установка обработчика кнопки входа
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    // Инициализация UI элементов
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvStatus = findViewById(R.id.tv_status);
        tvError = findViewById(R.id.tv_error);
    }

    // Проверка сохраненных данных и автоматический вход
    private void checkSavedCredentials() {
        if (prefsManager.hasCredentials() && prefsManager.getRememberMe()) {
            String username = prefsManager.getUsername();
            String password = prefsManager.getPassword();

            etUsername.setText(username);
            etPassword.setText(password);
            cbRememberMe.setChecked(true);

            // Автоматический вход
            attemptLogin();
        }
    }

    // Попытка входа в систему
    private void attemptLogin() {
        // Скрыть предыдущие ошибки
        hideError();

        // Получить введенные данные
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Валидация
        if (TextUtils.isEmpty(username)) {
            showError(getString(R.string.error_empty_username));
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError(getString(R.string.error_empty_password));
            etPassword.requestFocus();
            return;
        }

        // Показать прогресс
        showLoading(getString(R.string.checking_connection));

        // Инициализировать API клиент с credentials
        ApiClient.getInstance().initialize(username, password);
        RagApiService apiService = ApiClient.getInstance().getApiService();

        // Проверить подключение к серверу
        apiService.checkHealth().enqueue(new Callback<HealthResponse>() {
            @Override
            public void onResponse(Call<HealthResponse> call, Response<HealthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HealthResponse health = response.body();

                    if (health.isHealthy()) {
                        // Сервер доступен и RAG инициализирован
                        onLoginSuccess(username, password);
                    } else {
                        // Сервер доступен, но RAG не готов
                        hideLoading();
                        showError(getString(R.string.error_rag_not_initialized));
                    }
                } else {
                    // Ошибка авторизации (401) или другая ошибка сервера
                    hideLoading();
                    if (response.code() == 401) {
                        showError(getString(R.string.error_login_failed));
                    } else {
                        showError(getString(R.string.error_server_unavailable) + " (" + response.code() + ")");
                    }
                }
            }

            @Override
            public void onFailure(Call<HealthResponse> call, Throwable t) {
                hideLoading();

                // Обработка различных типов ошибок
                String errorMessage;
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = getString(R.string.error_timeout);
                } else if (t instanceof java.net.UnknownHostException ||
                           t instanceof java.net.ConnectException) {
                    errorMessage = getString(R.string.error_server_unavailable);
                } else {
                    errorMessage = getString(R.string.error_network) + "\n" + t.getMessage();
                }

                showError(errorMessage);
            }
        });
    }

    // Обработка успешного входа
    private void onLoginSuccess(String username, String password) {
        // Сохранить credentials если выбрано "Запомнить меня"
        if (cbRememberMe.isChecked()) {
            prefsManager.saveCredentials(username, password);
            prefsManager.setRememberMe(true);
        } else {
            prefsManager.clearCredentials();
            prefsManager.setRememberMe(false);
        }

        // Перейти на главный экран
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Закрыть LoginActivity
    }

    // Показать индикатор загрузки
    private void showLoading(String status) {
        btnLogin.setEnabled(false);
        btnLogin.setText("");
        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setText(status);
        tvStatus.setVisibility(View.VISIBLE);
        etUsername.setEnabled(false);
        etPassword.setEnabled(false);
    }

    // Скрыть индикатор загрузки
    private void hideLoading() {
        btnLogin.setEnabled(true);
        btnLogin.setText(R.string.btn_login);
        progressBar.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);
        etUsername.setEnabled(true);
        etPassword.setEnabled(true);
    }

    // Показать сообщение об ошибке
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    // Скрыть сообщение об ошибке
    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
