package com.company.ragsearch.ui.search;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.ragsearch.R;
import com.company.ragsearch.data.api.ApiClient;
import com.company.ragsearch.data.model.ImageInfo;
import com.company.ragsearch.data.model.SearchRequest;
import com.company.ragsearch.data.model.SearchResponse;
import com.company.ragsearch.utils.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private TextInputEditText queryInput;
    private MaterialButton searchButton;
    private LinearLayout loadingContainer;
    private LinearLayout resultsContainer;
    private LinearLayout errorContainer;
    private TextView answerText;
    private TextView errorText;
    private TextView imagesTitle;
    private LinearLayout imagesSection;
    private RecyclerView imagesRecyclerView;

    private ImagesAdapter imagesAdapter;
    private Call<SearchResponse> currentCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupAdapters();
        setupListeners();
    }

    private void initViews(View view) {
        queryInput = view.findViewById(R.id.queryInput);
        searchButton = view.findViewById(R.id.searchButton);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        resultsContainer = view.findViewById(R.id.resultsContainer);
        errorContainer = view.findViewById(R.id.errorContainer);
        answerText = view.findViewById(R.id.answerText);
        errorText = view.findViewById(R.id.errorText);
        imagesTitle = view.findViewById(R.id.imagesTitle);
        imagesSection = view.findViewById(R.id.imagesSection);
        imagesRecyclerView = view.findViewById(R.id.imagesRecyclerView);

        MaterialButton retryButton = view.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> performSearch());
    }

    private void setupAdapters() {
        imagesAdapter = new ImagesAdapter();
        imagesRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.setAdapter(imagesAdapter);

        imagesAdapter.setOnImageClickListener((image, position) -> {
            showFullScreenImage(image);
        });
    }

    private void setupListeners() {
        searchButton.setOnClickListener(v -> performSearch());

        queryInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = queryInput.getText() != null ? queryInput.getText().toString().trim() : "";

        if (query.isEmpty()) {
            queryInput.setError(getString(R.string.error_empty_username));
            return;
        }

        // Отмена предыдущего запроса
        if (currentCall != null) {
            currentCall.cancel();
        }

        showLoading();

        SearchRequest request = new SearchRequest(query, 5);
        currentCall = ApiClient.getInstance().getApiService().search(request);

        currentCall.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call,
                                   @NonNull Response<SearchResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    showResults(response.body());
                } else if (response.code() == 401) {
                    showError(getString(R.string.error_login_failed));
                } else if (response.code() == 500) {
                    showError(getString(R.string.error_server_error));
                } else {
                    showError(getString(R.string.error_server_unavailable) + " (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                if (!isAdded() || call.isCanceled()) return;
                showError(getString(R.string.error_network));
            }
        });
    }

    private void showLoading() {
        loadingContainer.setVisibility(View.VISIBLE);
        resultsContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        searchButton.setEnabled(false);
    }

    private void showResults(SearchResponse response) {
        loadingContainer.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        searchButton.setEnabled(true);

        answerText.setText(response.getAnswer());

        // Сохранить в историю
        String query = queryInput.getText() != null ? queryInput.getText().toString() : "";
        String answer = response.getAnswer();
        PreferencesManager.getInstance(requireContext()).addToHistory(query, answer, null);

        // Изображения
        if (response.getImages() != null && !response.getImages().isEmpty()) {
            imagesSection.setVisibility(View.VISIBLE);
            imagesTitle.setText(getString(R.string.images_title, response.getImages().size()));
            imagesAdapter.setImages(response.getImages());
        } else {
            imagesSection.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        loadingContainer.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        searchButton.setEnabled(true);
        errorText.setText(message);
    }

    private void showFullScreenImage(ImageInfo image) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ImageView imageView = new ImageView(requireContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (image.hasBase64()) {
            byte[] decodedBytes = Base64.decode(image.getBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(bitmap);
        }

        imageView.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(imageView);
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (currentCall != null) {
            currentCall.cancel();
        }
    }
}
