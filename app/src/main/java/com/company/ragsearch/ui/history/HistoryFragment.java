package com.company.ragsearch.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.ragsearch.R;
import com.company.ragsearch.data.model.HistoryItem;
import com.company.ragsearch.utils.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

// Фрагмент истории поиска
public class HistoryFragment extends Fragment {

    private RecyclerView historyRecyclerView;
    private LinearLayout emptyHistoryContainer;
    private MaterialButton btnClearHistory;

    private HistoryAdapter adapter;
    private PreferencesManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsManager = PreferencesManager.getInstance(requireContext());

        // Инициализация UI
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        emptyHistoryContainer = view.findViewById(R.id.emptyHistoryContainer);
        btnClearHistory = view.findViewById(R.id.btnClearHistory);

        // Настройка RecyclerView
        adapter = new HistoryAdapter();
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRecyclerView.setAdapter(adapter);

        // Обработчик клика по элементу
        adapter.setOnItemClickListener(this::showHistoryItemDialog);

        // Обработчик кнопки очистки
        btnClearHistory.setOnClickListener(v -> showClearHistoryDialog());

        // Загрузить историю
        loadHistory();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем историю при возврате на экран
        loadHistory();
    }

    // Загрузить историю из SharedPreferences
    private void loadHistory() {
        List<HistoryItem> history = prefsManager.getHistory();

        if (history.isEmpty()) {
            // Показать пустую историю
            historyRecyclerView.setVisibility(View.GONE);
            emptyHistoryContainer.setVisibility(View.VISIBLE);
            btnClearHistory.setEnabled(false);
        } else {
            // Показать список
            historyRecyclerView.setVisibility(View.VISIBLE);
            emptyHistoryContainer.setVisibility(View.GONE);
            btnClearHistory.setEnabled(true);
            adapter.setHistoryItems(history);
        }
    }

    // Показать диалог с полным ответом
    private void showHistoryItemDialog(HistoryItem item) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(item.getQuery())
                .setMessage(item.getAnswer())
                .setPositiveButton(R.string.ok, null)
                .setNeutralButton(R.string.copy, (dialog, which) -> {
                    // Копирование в буфер обмена
                    android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager) requireContext()
                                    .getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText(
                            item.getQuery(), item.getAnswer());
                    clipboard.setPrimaryClip(clip);

                    // Показать уведомление
                    android.widget.Toast.makeText(requireContext(),
                            "Скопировано в буфер обмена", android.widget.Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // Показать диалог подтверждения очистки истории
    private void showClearHistoryDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.clear_all_history)
                .setMessage("Вы уверены, что хотите очистить всю историю?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    prefsManager.clearHistory();
                    loadHistory();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
