package com.company.ragsearch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.company.ragsearch.data.model.HistoryItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    private static PreferencesManager instance;
    private final SharedPreferences encryptedPrefs;
    private final SharedPreferences normalPrefs;
    private final Gson gson;

    private PreferencesManager(Context context) {
        // Обычные настройки 
        normalPrefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        // Зашифрованные логин/пароль
        SharedPreferences encrypted = null;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encrypted = EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            encrypted = context.getSharedPreferences("secure_prefs_fallback", Context.MODE_PRIVATE);
        }
        this.encryptedPrefs = encrypted;
        this.gson = new Gson();
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context.getApplicationContext());
        }
        return instance;
    }


    public void saveCredentials(String username, String password) {
        encryptedPrefs.edit()
                .putString(Constants.KEY_USERNAME, username)
                .putString(Constants.KEY_PASSWORD, password)
                .apply();
    }

    public String getUsername() {
        return encryptedPrefs.getString(Constants.KEY_USERNAME, null);
    }

    public String getPassword() {
        return encryptedPrefs.getString(Constants.KEY_PASSWORD, null);
    }

    public boolean hasCredentials() {
        return getUsername() != null && getPassword() != null;
    }

    public void clearCredentials() {
        encryptedPrefs.edit()
                .remove(Constants.KEY_USERNAME)
                .remove(Constants.KEY_PASSWORD)
                .apply();
    }

    // Запомнить меня
    public void setRememberMe(boolean remember) {
        normalPrefs.edit().putBoolean(Constants.KEY_REMEMBER_ME, remember).apply();
    }

    public boolean getRememberMe() {
        return normalPrefs.getBoolean(Constants.KEY_REMEMBER_ME, false);
    }

    // Тема
    public void setTheme(int theme) {
        normalPrefs.edit().putInt(Constants.KEY_THEME, theme).apply();
    }

    public int getTheme() {
        return normalPrefs.getInt(Constants.KEY_THEME, Constants.THEME_SYSTEM);
    }

    private static final String KEY_HISTORY = "search_history";
    private static final int MAX_HISTORY_SIZE = 10;

    // Добавить запрос в историю
    public void addToHistory(String query, String answer, String bestInstructionTitle) {
        List<HistoryItem> history = getHistory();

        // Добавить новый элемент в начало
        HistoryItem newItem = new HistoryItem(query, answer, System.currentTimeMillis(), bestInstructionTitle);
        history.add(0, newItem);

        // Ограничить размер истории
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }

        // Сохранить
        saveHistory(history);
    }

    // Получить всю историю
    public List<HistoryItem> getHistory() {
        String json = normalPrefs.getString(KEY_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<HistoryItem>>(){}.getType();
        List<HistoryItem> history = gson.fromJson(json, type);
        return history != null ? history : new ArrayList<>();
    }

    // Сохранить историю
    private void saveHistory(List<HistoryItem> history) {
        String json = gson.toJson(history);
        normalPrefs.edit().putString(KEY_HISTORY, json).apply();
    }

    // Очистить историю
    public void clearHistory() {
        normalPrefs.edit().remove(KEY_HISTORY).apply();
    }

    public void clearAll() {
        encryptedPrefs.edit().clear().apply();
        normalPrefs.edit().clear().apply();
    }
}
