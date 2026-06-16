package com.example.smarttripvoyager.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

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
    private ProgressBar progressPublish;

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
        progressPublish  = findViewById(R.id.progressPublish);

        // Load initial preview for position 0
        refreshPreview(0);

        spinnerCategorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshPreview(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnPublish.setOnClickListener(v -> createPublication());
    }

    private void refreshPreview(int categoriePosition) {
        String[] values = getResources().getStringArray(R.array.publication_categories_values);
        if (categoriePosition >= values.length) return;
        String url = buildImageUrlForCategorie(values[categoriePosition]);
        Glide.with(this)
                .load(url)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(ivPostPreview);
        ivPostPreview.clearColorFilter();
    }

    private void createPublication() {
        String content = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        if (content.isEmpty()) {
            etDescription.setError("Veuillez écrire une description");
            etDescription.requestFocus();
            return;
        }

        String[] categorieValues = getResources().getStringArray(R.array.publication_categories_values);
        String categorie = categorieValues[spinnerCategorie.getSelectedItemPosition()];

        String[] cities = getResources().getStringArray(R.array.cities_fes_meknes);
        String ville = cities[spinnerVille.getSelectedItemPosition()];

        // Prepend city to content
        String fullContent = "📍 " + ville + " — " + content;

        String imageUrl = buildImageUrlForCategorie(categorie);

        Publication newPost = new Publication();
        newPost.setContenu(fullContent);
        newPost.setCategorie(categorie);
        newPost.setImageUrl(imageUrl);

        setPublishing(true);

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.createPublication(newPost).enqueue(new Callback<ApiResponse<Publication>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Publication>> call,
                                   @NonNull Response<ApiResponse<Publication>> response) {
                setPublishing(false);
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
                setPublishing(false);
                Toast.makeText(CreatePostActivity.this,
                        "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setPublishing(boolean loading) {
        progressPublish.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPublish.setEnabled(!loading);
        btnPublish.setText(loading ? "Publication en cours..." : "Publier maintenant");
        spinnerCategorie.setEnabled(!loading);
        spinnerVille.setEnabled(!loading);
        etDescription.setEnabled(!loading);
    }

    /** Genere une URL LoremFlickr thematisee selon la categorie. */
    private String buildImageUrlForCategorie(String categorie) {
        String tags;
        switch (categorie) {
            case "culture":      tags = "morocco,culture,medina"; break;
            case "nature":       tags = "morocco,nature,landscape"; break;
            case "food":         tags = "morocco,food,tagine"; break;
            case "adventure":    tags = "morocco,adventure,atlas"; break;
            case "history":      tags = "morocco,history,ruins"; break;
            case "wellness":     tags = "morocco,wellness,spa"; break;
            case "shopping":     tags = "morocco,shopping,souk"; break;
            case "festivals":    tags = "morocco,festival,celebration"; break;
            case "restaurants":  tags = "morocco,restaurant,cuisine"; break;
            case "monuments":    tags = "morocco,monument,architecture"; break;
            default:             tags = "morocco,travel"; break;
        }
        int lock = 300 + (int) (Math.random() * 9000);
        return "https://loremflickr.com/640/480/" + tags + "?lock=" + lock;
    }
}
