package com.example.smarttripvoyager.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.network.TokenManager;
import com.example.smarttripvoyager.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private MaterialButton btnSettings, btnLogout;
    private androidx.recyclerview.widget.RecyclerView recyclerViewMyPosts;
    private com.example.smarttripvoyager.ui.adapter.PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSettings = findViewById(R.id.btnSettings);
        btnLogout = findViewById(R.id.btnLogout);

        btnSettings.setOnClickListener(v -> {
            android.widget.EditText input = new android.widget.EditText(this);
            input.setHint("Nouveau pseudo");
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Modifier le profil")
                .setView(input)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String newName = input.getText().toString();
                    if (!newName.isEmpty()) {
                        java.util.Map<String, String> body = new java.util.HashMap<>();
                        body.put("nom", newName);
                        com.example.smarttripvoyager.network.RetrofitClient.getApiService(this)
                            .updateMe(body).enqueue(new retrofit2.Callback<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.UserDto>>() {
                                @Override
                                public void onResponse(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.UserDto>> call, retrofit2.Response<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.UserDto>> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                                        android.widget.TextView tvName = findViewById(R.id.tvProfileName);
                                        tvName.setText(response.body().data.nom);
                                        Toast.makeText(ProfileActivity.this, "Pseudo mis à jour", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.UserDto>> call, Throwable t) {}
                            });
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
        });

        btnLogout.setOnClickListener(v -> {
            TokenManager tokenManager = new TokenManager(this);
            tokenManager.clearToken();
            
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        recyclerViewMyPosts = findViewById(R.id.recyclerViewMyPosts);
        recyclerViewMyPosts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        postAdapter = new com.example.smarttripvoyager.ui.adapter.PostAdapter(this, new java.util.ArrayList<>());
        recyclerViewMyPosts.setAdapter(postAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
        loadMyPosts();
    }

    private void loadUserProfile() {
        com.example.smarttripvoyager.network.ApiService apiService = com.example.smarttripvoyager.network.RetrofitClient.getApiService(this);
        apiService.getMe().enqueue(new retrofit2.Callback<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>> call, retrofit2.Response<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    com.example.smarttripvoyager.data.model.UserDto user = response.body().data.user;
                    if (user != null) {
                        android.widget.TextView tvName = findViewById(R.id.tvProfileName);
                        android.widget.TextView tvEmail = findViewById(R.id.tvProfileEmail);
                        android.widget.TextView tvLocation = findViewById(R.id.tvProfileLocation);
                        tvName.setText(user.nom);
                        tvEmail.setText(user.email);

                        StringBuilder location = new StringBuilder();
                        if (user.ville != null && !user.ville.isEmpty()) location.append(user.ville);
                        if (user.pays != null && !user.pays.isEmpty()) {
                            if (location.length() > 0) location.append(", ");
                            location.append(user.pays);
                        }
                        if (user.preferences != null && !user.preferences.isEmpty()) {
                            if (location.length() > 0) location.append(" • ");
                            location.append(user.preferences);
                        }
                        tvLocation.setText(location.toString());

                        // Load stats
                        loadStats();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Erreur chargement profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadStats() {
        // Here we would call apiService.getMyStats() and update the UI
        // Since we don't have the explicit model in Android yet, we use a simple map
        com.example.smarttripvoyager.network.ApiService apiService = com.example.smarttripvoyager.network.RetrofitClient.getApiService(this);
        apiService.getMyStats().enqueue(new retrofit2.Callback<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Long>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Long>>> call, retrofit2.Response<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Long>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.Map<String, Long> stats = response.body().data;
                    if (stats != null) {
                        android.widget.TextView tvPosts = findViewById(R.id.tvPostsCount);
                        android.widget.TextView tvLikes = findViewById(R.id.tvLikesCount);
                        android.widget.TextView tvSaved = findViewById(R.id.tvSavedCount);
                        
                        if (tvPosts != null) tvPosts.setText(String.valueOf(stats.getOrDefault("postsCount", 0L)));
                        if (tvLikes != null) tvLikes.setText(String.valueOf(stats.getOrDefault("likesReceived", 0L)));
                        if (tvSaved != null) tvSaved.setText(String.valueOf(stats.getOrDefault("savedCount", 0L)));
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Long>>> call, Throwable t) {}
        });
    }

    private void loadMyPosts() {
        com.example.smarttripvoyager.network.ApiService apiService = com.example.smarttripvoyager.network.RetrofitClient.getApiService(this);
        apiService.getMyPosts().enqueue(new retrofit2.Callback<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<com.example.smarttripvoyager.data.model.Publication>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<com.example.smarttripvoyager.data.model.Publication>>> call, retrofit2.Response<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<com.example.smarttripvoyager.data.model.Publication>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    postAdapter.setPublications(response.body().data.content);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<com.example.smarttripvoyager.data.model.Publication>>> call, Throwable t) {}
        });
    }
}
