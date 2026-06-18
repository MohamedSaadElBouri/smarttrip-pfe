package com.example.smarttripvoyager.ui.home;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.ApiResponse;
import com.example.smarttripvoyager.data.model.Publication;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private TextInputEditText etDescription;
    private Spinner spinnerCategorie;
    private Spinner spinnerVille;
    private MaterialButton btnPublish;
    private android.widget.ImageView ivPostPreview;
    private MaterialCardView cardImagePreview;
    private android.widget.TextView tvImageHint;
    private ProgressBar progressPublish;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).centerCrop().into(ivPostPreview);
                    ivPostPreview.clearColorFilter();
                    tvImageHint.setText("Toucher pour changer l'image");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etDescription    = findViewById(R.id.etDescription);
        spinnerCategorie = findViewById(R.id.spinnerCategorie);
        spinnerVille     = findViewById(R.id.spinnerVille);
        btnPublish       = findViewById(R.id.btnPublish);
        ivPostPreview    = findViewById(R.id.ivPostPreview);
        cardImagePreview = findViewById(R.id.cardImagePreview);
        tvImageHint      = findViewById(R.id.tvImageHint);
        progressPublish  = findViewById(R.id.progressPublish);

        cardImagePreview.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnPublish.setOnClickListener(v -> createPublication());
    }

    private void createPublication() {
        String content = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        if (content.isEmpty()) {
            etDescription.setError("Veuillez écrire une description");
            etDescription.requestFocus();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Veuillez choisir une image depuis la galerie", Toast.LENGTH_SHORT).show();
            return;
        }

        setPublishing(true, "Envoi de l'image...");
        uploadImageThenPublish(content);
    }

    private void uploadImageThenPublish(String content) {
        byte[] bytes;
        String mimeType;
        try {
            mimeType = getContentResolver().getType(selectedImageUri);
            if (mimeType == null) mimeType = "image/jpeg";
            bytes = readBytes(selectedImageUri);
        } catch (IOException e) {
            setPublishing(false, null);
            Toast.makeText(this, "Impossible de lire l'image sélectionnée", Toast.LENGTH_LONG).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(bytes, MediaType.parse(mimeType));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "post.jpg", requestBody);

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.uploadFile(filePart).enqueue(new Callback<ApiResponse<java.util.Map<String, String>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<java.util.Map<String, String>>> call,
                                    @NonNull Response<ApiResponse<java.util.Map<String, String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    String imageUrl = response.body().data.get("url");
                    setPublishing(true, "Publication en cours...");
                    publish(content, imageUrl);
                } else {
                    setPublishing(false, null);
                    Toast.makeText(CreatePostActivity.this,
                            "Erreur lors de l'envoi de l'image (code " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<java.util.Map<String, String>>> call, @NonNull Throwable t) {
                setPublishing(false, null);
                Toast.makeText(CreatePostActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void publish(String content, String imageUrl) {
        String[] categorieValues = getResources().getStringArray(R.array.publication_categories_values);
        String categorie = categorieValues[spinnerCategorie.getSelectedItemPosition()];

        String[] cities = getResources().getStringArray(R.array.cities_fes_meknes);
        String ville = cities[spinnerVille.getSelectedItemPosition()];

        String fullContent = "📍 " + ville + " — " + content;

        Publication newPost = new Publication();
        newPost.setContenu(fullContent);
        newPost.setCategorie(categorie);
        newPost.setImageUrl(imageUrl);

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.createPublication(newPost).enqueue(new Callback<ApiResponse<Publication>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Publication>> call,
                                   @NonNull Response<ApiResponse<Publication>> response) {
                setPublishing(false, null);
                if (response.isSuccessful()) {
                    Toast.makeText(CreatePostActivity.this,
                            "Publication partagée avec succès !", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreatePostActivity.this,
                            "Erreur lors de la publication (code " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Publication>> call, @NonNull Throwable t) {
                setPublishing(false, null);
                Toast.makeText(CreatePostActivity.this,
                        "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private byte[] readBytes(Uri uri) throws IOException {
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) throw new IOException("Impossible d'ouvrir l'image");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    private void setPublishing(boolean loading, String label) {
        progressPublish.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPublish.setEnabled(!loading);
        btnPublish.setText(loading ? label : "Publier maintenant");
        spinnerCategorie.setEnabled(!loading);
        spinnerVille.setEnabled(!loading);
        etDescription.setEnabled(!loading);
        cardImagePreview.setEnabled(!loading);
    }
}
