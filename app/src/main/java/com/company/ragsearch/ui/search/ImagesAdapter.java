package com.company.ragsearch.ui.search;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.ragsearch.R;
import com.company.ragsearch.data.model.ImageInfo;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<ImageInfo> images = new ArrayList<>();
    private OnImageClickListener clickListener;

    public interface OnImageClickListener {
        void onImageClick(ImageInfo image, int position);
    }

    public void setImages(List<ImageInfo> images) {
        this.images = images != null ? images : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageInfo image = images.get(position);
        holder.bind(image);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onImageClick(image, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(ImageInfo image) {
            // Декодируем base64 и отображаем
            String base64Data = image.getBase64();
            if (base64Data != null && !base64Data.isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    imageView.setImageResource(R.drawable.ic_image_placeholder);
                }
            } else {
                imageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        }
    }
}
