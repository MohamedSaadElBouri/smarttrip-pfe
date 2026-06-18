package com.example.smarttripvoyager.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.ApiResponse;
import com.example.smarttripvoyager.data.model.AuthResponse;
import com.example.smarttripvoyager.data.model.Circuit;
import com.example.smarttripvoyager.data.model.Page;
import com.example.smarttripvoyager.data.model.Publication;
import com.example.smarttripvoyager.data.model.UserDto;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.example.smarttripvoyager.network.TokenManager;
import com.example.smarttripvoyager.ui.adapter.PostAdapter;
import com.example.smarttripvoyager.ui.auth.LoginActivity;
import com.example.smarttripvoyager.ui.circuit.CircuitDetailActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail, tvProfileLocation;
    private TextView tvPostsCount, tvLikesCount, tvCommentsCount;
    private TextView tvSavedCount, tvSavedCircuitsCount;
    private TextView tvSavedPostsEmpty, tvSavedCircuitsEmpty;
    private MaterialButton btnSettings, btnLogout;
    private SwipeRefreshLayout swipeRefresh;
    private ImageView ivProfileAvatar, ivEditAvatarBadge;

    private final ActivityResultLauncher<String> pickAvatarLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) uploadAvatar(uri);
            });

    private RecyclerView recyclerViewMyPosts;
    private RecyclerView recyclerViewSavedPosts;
    private RecyclerView recyclerViewSavedCircuits;

    private PostAdapter postAdapter;
    private PostAdapter savedPostsAdapter;
    private SavedCircuitMiniAdapter savedCircuitsAdapter;

    private int pendingLoads = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvProfileName         = findViewById(R.id.tvProfileName);
        tvProfileEmail        = findViewById(R.id.tvProfileEmail);
        tvProfileLocation     = findViewById(R.id.tvProfileLocation);
        tvPostsCount          = findViewById(R.id.tvPostsCount);
        tvLikesCount          = findViewById(R.id.tvLikesCount);
        tvCommentsCount       = findViewById(R.id.tvCommentsCount);
        tvSavedCount          = findViewById(R.id.tvSavedCount);
        tvSavedCircuitsCount  = findViewById(R.id.tvSavedCircuitsCount);
        tvSavedPostsEmpty     = findViewById(R.id.tvSavedPostsEmpty);
        tvSavedCircuitsEmpty  = findViewById(R.id.tvSavedCircuitsEmpty);
        btnSettings           = findViewById(R.id.btnSettings);
        btnLogout             = findViewById(R.id.btnLogout);
        swipeRefresh          = findViewById(R.id.swipeRefresh);
        ivProfileAvatar       = findViewById(R.id.ivProfileAvatar);
        ivEditAvatarBadge     = findViewById(R.id.ivEditAvatarBadge);

        View.OnClickListener pickAvatar = v -> pickAvatarLauncher.launch("image/*");
        ivProfileAvatar.setOnClickListener(pickAvatar);
        ivEditAvatarBadge.setOnClickListener(pickAvatar);

        // My posts
        recyclerViewMyPosts = findViewById(R.id.recyclerViewMyPosts);
        recyclerViewMyPosts.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(this, new ArrayList<>());
        recyclerViewMyPosts.setAdapter(postAdapter);

        // Saved posts
        recyclerViewSavedPosts = findViewById(R.id.recyclerViewSavedPosts);
        recyclerViewSavedPosts.setLayoutManager(new LinearLayoutManager(this));
        savedPostsAdapter = new PostAdapter(this, new ArrayList<>());
        recyclerViewSavedPosts.setAdapter(savedPostsAdapter);

        // Saved circuits
        recyclerViewSavedCircuits = findViewById(R.id.recyclerViewSavedCircuits);
        recyclerViewSavedCircuits.setLayoutManager(new LinearLayoutManager(this));
        savedCircuitsAdapter = new SavedCircuitMiniAdapter(new ArrayList<>());
        recyclerViewSavedCircuits.setAdapter(savedCircuitsAdapter);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::refreshAll);

        btnSettings.setOnClickListener(v -> {
            android.widget.EditText input = new android.widget.EditText(this);
            input.setHint("Nouveau pseudo");
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Modifier le profil")
                .setView(input)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        java.util.Map<String, String> body = new java.util.HashMap<>();
                        body.put("nom", newName);
                        RetrofitClient.getApiService(this)
                            .updateMe(body)
                            .enqueue(new Callback<ApiResponse<UserDto>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<UserDto>> call,
                                                       Response<ApiResponse<UserDto>> response) {
                                    if (response.isSuccessful() && response.body() != null
                                            && response.body().data != null) {
                                        tvProfileName.setText(response.body().data.nom);
                                        Toast.makeText(ProfileActivity.this,
                                                "Pseudo mis à jour", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {}
                            });
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
        });

        btnLogout.setOnClickListener(v -> {
            new TokenManager(this).clearToken();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAll();
    }

    private void refreshAll() {
        pendingLoads = 4;
        swipeRefresh.setRefreshing(true);
        loadUserProfile();
        loadMyPosts();
        loadSavedPublications();
        loadSavedCircuits();
    }

    private void onLoadDone() {
        pendingLoads--;
        if (pendingLoads <= 0) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void loadUserProfile() {
        ApiService api = RetrofitClient.getApiService(this);
        api.getMe().enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                   Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().data != null) {
                    UserDto user = response.body().data.user;
                    if (user != null) {
                        tvProfileName.setText(user.nom != null && !user.nom.isEmpty()
                                ? user.nom : user.email);
                        tvProfileEmail.setText(user.email);

                        StringBuilder location = new StringBuilder();
                        if (user.ville != null && !user.ville.isEmpty())
                            location.append(user.ville);
                        if (user.pays != null && !user.pays.isEmpty()) {
                            if (location.length() > 0) location.append(", ");
                            location.append(user.pays);
                        }
                        if (user.preferences != null && !user.preferences.isEmpty()) {
                            if (location.length() > 0) location.append(" • ");
                            location.append(user.preferences);
                        }
                        tvProfileLocation.setText(location.toString());
                        displayAvatar(user.photoUrl);

                        loadStats();
                        return;
                    }
                }
                onLoadDone();
                Toast.makeText(ProfileActivity.this,
                        "Erreur chargement profil", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                onLoadDone();
                Toast.makeText(ProfileActivity.this,
                        "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStats() {
        RetrofitClient.getApiService(this)
            .getMyStats()
            .enqueue(new Callback<ApiResponse<Map<String, Long>>>() {
                @Override
                public void onResponse(Call<ApiResponse<Map<String, Long>>> call,
                                       Response<ApiResponse<Map<String, Long>>> response) {
                    onLoadDone();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().data != null) {
                        Map<String, Long> s = response.body().data;
                        setStat(tvPostsCount,         s.getOrDefault("postsCount", 0L));
                        setStat(tvLikesCount,         s.getOrDefault("likesGivenCount", 0L));
                        setStat(tvCommentsCount,      s.getOrDefault("commentsCount", 0L));
                        setStat(tvSavedCount,         s.getOrDefault("savedCount", 0L));
                        setStat(tvSavedCircuitsCount, s.getOrDefault("savedCircuitsCount", 0L));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Map<String, Long>>> call, Throwable t) {
                    onLoadDone();
                }
            });
    }

    private void setStat(TextView tv, long value) {
        if (tv != null) tv.setText(String.valueOf(value));
    }

    private void displayAvatar(String photoUrl) {
        if (photoUrl != null && !photoUrl.isEmpty()) {
            ivProfileAvatar.setPadding(0, 0, 0, 0);
            ivProfileAvatar.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            Glide.with(this).load(photoUrl).circleCrop().into(ivProfileAvatar);
        } else {
            ivProfileAvatar.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE);
            int pad = (int) (20 * getResources().getDisplayMetrics().density);
            ivProfileAvatar.setPadding(pad, pad, pad, pad);
            ivProfileAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
        }
    }

    private void uploadAvatar(Uri uri) {
        byte[] bytes;
        String mimeType;
        try {
            mimeType = getContentResolver().getType(uri);
            if (mimeType == null) mimeType = "image/jpeg";
            try (java.io.InputStream in = getContentResolver().openInputStream(uri)) {
                if (in == null) throw new java.io.IOException("Impossible d'ouvrir l'image");
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
                bytes = out.toByteArray();
            }
        } catch (java.io.IOException e) {
            Toast.makeText(this, "Impossible de lire l'image sélectionnée", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Mise à jour de la photo de profil...", Toast.LENGTH_SHORT).show();

        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(bytes, okhttp3.MediaType.parse(mimeType));
        okhttp3.MultipartBody.Part filePart = okhttp3.MultipartBody.Part.createFormData("file", "avatar.jpg", requestBody);

        RetrofitClient.getApiService(this).uploadFile(filePart)
            .enqueue(new Callback<ApiResponse<Map<String, String>>>() {
                @Override
                public void onResponse(Call<ApiResponse<Map<String, String>>> call,
                                       Response<ApiResponse<Map<String, String>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        String url = response.body().data.get("url");
                        saveAvatarUrl(url);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Erreur lors de l'envoi de l'image", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Map<String, String>>> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void saveAvatarUrl(String url) {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("photoUrl", url);
        RetrofitClient.getApiService(this).updateMe(body)
            .enqueue(new Callback<ApiResponse<UserDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        displayAvatar(response.body().data.photoUrl);
                        Toast.makeText(ProfileActivity.this, "Photo de profil mise à jour", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void loadMyPosts() {
        RetrofitClient.getApiService(this)
            .getMyPosts()
            .enqueue(new Callback<ApiResponse<Page<Publication>>>() {
                @Override
                public void onResponse(Call<ApiResponse<Page<Publication>>> call,
                                       Response<ApiResponse<Page<Publication>>> response) {
                    onLoadDone();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().data != null) {
                        postAdapter.setPublications(response.body().data.content);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Page<Publication>>> call, Throwable t) {
                    onLoadDone();
                }
            });
    }

    private void loadSavedPublications() {
        RetrofitClient.getApiService(this)
            .getSavedPublications()
            .enqueue(new Callback<ApiResponse<List<Publication>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Publication>>> call,
                                       Response<ApiResponse<List<Publication>>> response) {
                    onLoadDone();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().data != null) {
                        List<Publication> saved = response.body().data;
                        savedPostsAdapter.setPublications(saved);
                        tvSavedPostsEmpty.setVisibility(saved.isEmpty() ? View.VISIBLE : View.GONE);
                        recyclerViewSavedPosts.setVisibility(saved.isEmpty() ? View.GONE : View.VISIBLE);
                    } else {
                        tvSavedPostsEmpty.setVisibility(View.VISIBLE);
                        recyclerViewSavedPosts.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Publication>>> call, Throwable t) {
                    onLoadDone();
                    tvSavedPostsEmpty.setVisibility(View.VISIBLE);
                    recyclerViewSavedPosts.setVisibility(View.GONE);
                }
            });
    }

    private void loadSavedCircuits() {
        RetrofitClient.getApiService(this)
            .getMesCircuits()
            .enqueue(new Callback<ApiResponse<List<Circuit>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Circuit>>> call,
                                       Response<ApiResponse<List<Circuit>>> response) {
                    onLoadDone();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().data != null) {
                        List<Circuit> circuits = response.body().data;
                        savedCircuitsAdapter.setCircuits(circuits);
                        tvSavedCircuitsEmpty.setVisibility(circuits.isEmpty() ? View.VISIBLE : View.GONE);
                        recyclerViewSavedCircuits.setVisibility(circuits.isEmpty() ? View.GONE : View.VISIBLE);
                    } else {
                        tvSavedCircuitsEmpty.setVisibility(View.VISIBLE);
                        recyclerViewSavedCircuits.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Circuit>>> call, Throwable t) {
                    onLoadDone();
                    tvSavedCircuitsEmpty.setVisibility(View.VISIBLE);
                    recyclerViewSavedCircuits.setVisibility(View.GONE);
                }
            });
    }

    private static class SavedCircuitMiniAdapter
            extends RecyclerView.Adapter<SavedCircuitMiniAdapter.ViewHolder> {

        private final List<Circuit> circuits;

        SavedCircuitMiniAdapter(List<Circuit> circuits) {
            this.circuits = circuits;
        }

        void setCircuits(List<Circuit> newCircuits) {
            circuits.clear();
            circuits.addAll(newCircuits);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_saved_circuit_mini, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Circuit circuit = circuits.get(position);
            String titre = circuit.getTitre() != null ? circuit.getTitre() : "Circuit personnalisé";
            holder.tvTitle.setText(titre);

            String detail = "";
            if (circuit.getVille() != null) detail += circuit.getVille();
            if (circuit.getDureeJours() != null) {
                if (!detail.isEmpty()) detail += " • ";
                detail += circuit.getDureeJours() + " jours";
            }
            if (circuit.getPrixEstime() != null) {
                if (!detail.isEmpty()) detail += " • ";
                detail += circuit.getPrixEstime().intValue() + " MAD";
            }
            holder.tvDetail.setText(detail);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CircuitDetailActivity.class);
                intent.putExtra("circuit_id", circuit.getId());
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return circuits.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDetail;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle  = itemView.findViewById(R.id.tvCircuitTitle);
                tvDetail = itemView.findViewById(R.id.tvCircuitDetail);
            }
        }
    }
}
