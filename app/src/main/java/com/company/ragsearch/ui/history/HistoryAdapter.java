package com.company.ragsearch.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.ragsearch.R;
import com.company.ragsearch.data.model.HistoryItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryItem> historyItems = new ArrayList<>();
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public void setOnItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    public void setHistoryItems(List<HistoryItem> items) {
        this.historyItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(historyItems.get(position));
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuery;
        private final TextView tvInstructionTitle;
        private final TextView tvTimestamp;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuery = itemView.findViewById(R.id.tvQuery);
            tvInstructionTitle = itemView.findViewById(R.id.tvInstructionTitle);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(HistoryItem item) {
            tvQuery.setText(item.getQuery());

            // Название инструкции
            if (item.getBestInstructionTitle() != null && !item.getBestInstructionTitle().isEmpty()) {
                tvInstructionTitle.setVisibility(View.VISIBLE);
                tvInstructionTitle.setText(item.getBestInstructionTitle());
            } else {
                tvInstructionTitle.setVisibility(View.GONE);
            }

            // Форматирование времени
            tvTimestamp.setText(formatTime(item.getTimestamp()));

            // Обработчик клика
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }

        private String formatTime(long timestamp) {
            long diff = System.currentTimeMillis() - timestamp;
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                return days + " " + getDaysString(days) + " назад";
            } else if (hours > 0) {
                return hours + " " + getHoursString(hours) + " назад";
            } else if (minutes > 0) {
                return minutes + " " + getMinutesString(minutes) + " назад";
            } else {
                return "только что";
            }
        }

        private String getDaysString(long days) {
            if (days == 1) return "день";
            if (days >= 2 && days <= 4) return "дня";
            return "дней";
        }

        private String getHoursString(long hours) {
            if (hours == 1 || hours == 21) return "час";
            if ((hours >= 2 && hours <= 4) || (hours >= 22 && hours <= 24)) return "часа";
            return "часов";
        }

        private String getMinutesString(long minutes) {
            long lastDigit = minutes % 10;
            long lastTwoDigits = minutes % 100;

            if (lastTwoDigits >= 11 && lastTwoDigits <= 14) return "минут";
            if (lastDigit == 1) return "минуту";
            if (lastDigit >= 2 && lastDigit <= 4) return "минуты";
            return "минут";
        }
    }
}
