package com.company.ragsearch;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.company.ragsearch.utils.Constants;
import com.company.ragsearch.utils.PreferencesManager;

public class RagSearchApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Применить сохранённую тему при запуске
        applyTheme();
    }

    // Применить тему из настроек
    private void applyTheme() {
        PreferencesManager prefsManager = PreferencesManager.getInstance(this);
        int theme = prefsManager.getTheme();

        int nightMode;
        if (theme == Constants.THEME_LIGHT) {
            nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else if (theme == Constants.THEME_DARK) {
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }

        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
