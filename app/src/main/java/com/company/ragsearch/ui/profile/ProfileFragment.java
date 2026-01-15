package com.company.ragsearch.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.company.ragsearch.R;
import com.company.ragsearch.data.api.ApiClient;
import com.company.ragsearch.ui.auth.LoginActivity;
import com.company.ragsearch.utils.Constants;
import com.company.ragsearch.utils.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

// Фрагмент профиля и настроек приложения
public class ProfileFragment extends Fragment {

    private RadioGroup themeRadioGroup;
    private TextView tvAppVersion;
    private MaterialButton logoutButton;

    private PreferencesManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsManager = PreferencesManager.getInstance(requireContext());

        // Инициализация UI
        themeRadioGroup = view.findViewById(R.id.themeRadioGroup);
        tvAppVersion = view.findViewById(R.id.tvAppVersion);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Установить текущую тему
        setupTheme();

        // Установить версию приложения
        setupAppVersion();

        // Обработчики
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> onThemeChanged(checkedId));
        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }

    // Настройка переключателя темы
    private void setupTheme() {
        int currentTheme = prefsManager.getTheme();

        if (currentTheme == Constants.THEME_LIGHT) {
            themeRadioGroup.check(R.id.radioLight);
        } else if (currentTheme == Constants.THEME_DARK) {
            themeRadioGroup.check(R.id.radioDark);
        } else {
            themeRadioGroup.check(R.id.radioSystem);
        }
    }

    // Обработка изменения темы
    private void onThemeChanged(int checkedId) {
        int newTheme;
        int nightMode;

        if (checkedId == R.id.radioLight) {
            newTheme = Constants.THEME_LIGHT;
            nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else if (checkedId == R.id.radioDark) {
            newTheme = Constants.THEME_DARK;
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            newTheme = Constants.THEME_SYSTEM;
            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }

        // Сохранить выбор
        prefsManager.setTheme(newTheme);

        // Применить тему
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    // Установка версии приложения
    private void setupAppVersion() {
        try {
            PackageInfo packageInfo = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            String version = packageInfo.versionName;
            tvAppVersion.setText(getString(R.string.app_version, version));
        } catch (PackageManager.NameNotFoundException e) {
            tvAppVersion.setText(getString(R.string.app_version, "1.0.0"));
        }
    }

    // Показать диалог подтверждения выхода
    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.btn_logout)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.yes, (dialog, which) -> logout())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // Выход из системы
    private void logout() {
        // Очистить credentials
        prefsManager.clearCredentials();

        // Сбросить API клиент
        ApiClient.getInstance().reset();

        // Перейти на экран входа
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
