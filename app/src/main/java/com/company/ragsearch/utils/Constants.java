package com.company.ragsearch.utils;

public class Constants {

    public static final String BASE_URL = "http://100.87.151.67:8000/";

    // Общее
    public static final String PREFS_NAME = "RagSearchPrefs";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER_ME = "remember_me";
    public static final String KEY_THEME = "theme";

    // Таймауты запросов
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 180;
    public static final int WRITE_TIMEOUT = 30;

    // Темы
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;
}
