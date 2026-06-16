package com.example.smarttripvoyager.ui.circuit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.ApiResponse;
import com.example.smarttripvoyager.data.model.Circuit;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.example.smarttripvoyager.util.InteractionTracker;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CircuitDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView ivCircuitImage;
    private TextView tvCircuitTitle, tvCircuitCity, tvCircuitDescription, tvReason, tvErrorMessage;
    private Chip chipTheme, chipDuration, chipBudget;
    private MaterialButton btnSaveOffline, btnRetry;
    private ProgressBar progressCircuit;
    private LinearLayout layoutError, layoutReason, layoutItinerary;
    private View nestedScrollContent;
    private RecyclerView recyclerViewEtapes;

    private Circuit currentCircuit;
    private long circuitId = -1;
    private String aiReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        collapsingToolbar  = findViewById(R.id.collapsingToolbar);
        ivCircuitImage     = findViewById(R.id.ivCircuitImage);
        tvCircuitTitle     = findViewById(R.id.tvCircuitTitle);
        tvCircuitCity      = findViewById(R.id.tvCircuitCity);
        tvCircuitDescription = findViewById(R.id.tvCircuitDescription);
        tvReason           = findViewById(R.id.tvReason);
        tvErrorMessage     = findViewById(R.id.tvErrorMessage);
        chipTheme          = findViewById(R.id.chipTheme);
        chipDuration       = findViewById(R.id.chipDuration);
        chipBudget         = findViewById(R.id.chipBudget);
        btnSaveOffline     = findViewById(R.id.btnSaveOffline);
        btnRetry           = findViewById(R.id.btnRetry);
        progressCircuit    = findViewById(R.id.progressCircuit);
        layoutError        = findViewById(R.id.layoutError);
        layoutReason       = findViewById(R.id.layoutReason);
        layoutItinerary    = findViewById(R.id.layoutItinerary);
        nestedScrollContent = findViewById(R.id.nestedScrollContent);
        recyclerViewEtapes = findViewById(R.id.recyclerViewEtapes);

        recyclerViewEtapes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEtapes.setNestedScrollingEnabled(false);

        btnSaveOffline.setOnClickListener(v -> saveToMyTrips());
        btnRetry.setOnClickListener(v -> loadCircuit(circuitId));

        circuitId = getIntent().getLongExtra("circuit_id", -1);
        aiReason  = getIntent().getStringExtra("ai_reason");

        if (circuitId == -1) {
            Toast.makeText(this, "Circuit introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadCircuit(circuitId);
    }

    private void showLoading() {
        progressCircuit.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        nestedScrollContent.setVisibility(View.GONE);
    }

    private void showError(String message) {
        progressCircuit.setVisibility(View.GONE);
        nestedScrollContent.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
    }

    private void showContent() {
        progressCircuit.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        nestedScrollContent.setVisibility(View.VISIBLE);
    }

    private void loadCircuit(long id) {
        showLoading();
        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getCircuit(id).enqueue(new Callback<ApiResponse<Circuit>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Circuit>> call,
                                   @NonNull Response<ApiResponse<Circuit>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    currentCircuit = response.body().data;
                    displayCircuit(currentCircuit);
                    InteractionTracker.logView(CircuitDetailActivity.this, "CIRCUIT", currentCircuit.getId());
                } else {
                    showError("Ce circuit n'est pas disponible pour le moment.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Circuit>> call, @NonNull Throwable t) {
                showError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    private void displayCircuit(Circuit c) {
        // Collapsing toolbar title
        collapsingToolbar.setTitle(c.getTitre() != null ? c.getTitre() : "Circuit");

        // Hero image
        if (c.getPhotoUrl() != null && !c.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(c.getPhotoUrl())
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(ivCircuitImage);
        }

        // Title
        tvCircuitTitle.setText(c.getTitre());

        // City
        String city = c.getVille() != null ? c.getVille() : "Maroc";
        tvCircuitCity.setText("📍 " + city);

        // Theme chip
        chipTheme.setText(c.getTheme() != null ? c.getTheme() : "Culturel");

        // Duration chip
        int jours = c.getDureeJours() != null ? c.getDureeJours() : 0;
        chipDuration.setText("⏱ " + jours + (jours > 1 ? " Jours" : " Jour"));

        // Budget chip
        if (c.getPrixEstime() != null && c.getPrixEstime() > 0) {
            chipBudget.setText("💰 " + c.getPrixEstime().intValue() + " MAD");
            chipBudget.setVisibility(View.VISIBLE);
        } else {
            chipBudget.setVisibility(View.GONE);
        }

        // Description
        tvCircuitDescription.setText(c.getDescription() != null ? c.getDescription() : "");

        // AI reason (from intent or from circuit)
        String reason = aiReason;
        if (reason != null && !reason.isBlank()) {
            tvReason.setText(reason);
            layoutReason.setVisibility(View.VISIBLE);
        } else {
            layoutReason.setVisibility(View.GONE);
        }

        // Itinerary steps
        List<Circuit.Etape> etapes = c.getEtapes();
        if (etapes != null && !etapes.isEmpty()) {
            recyclerViewEtapes.setAdapter(new EtapeAdapter(etapes));
            layoutItinerary.setVisibility(View.VISIBLE);
        } else {
            layoutItinerary.setVisibility(View.GONE);
        }

        showContent();
    }

    private void saveToMyTrips() {
        if (currentCircuit == null) return;
        btnSaveOffline.setEnabled(false);
        btnSaveOffline.setText("Enregistrement...");

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.rejoindreCircuit(currentCircuit.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                                   @NonNull Response<ApiResponse<Void>> response) {
                btnSaveOffline.setEnabled(true);
                if (response.isSuccessful()) {
                    btnSaveOffline.setText("✅ Enregistré dans Mes Circuits");
                    btnSaveOffline.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    getResources().getColor(R.color.success, getTheme())));
                    Toast.makeText(CircuitDetailActivity.this,
                            "Circuit ajouté à Mes Circuits !", Toast.LENGTH_LONG).show();
                } else if (response.code() == 400) {
                    btnSaveOffline.setText("✅ Déjà dans Mes Circuits");
                    Toast.makeText(CircuitDetailActivity.this,
                            "Ce circuit est déjà dans Mes circuits", Toast.LENGTH_SHORT).show();
                } else {
                    btnSaveOffline.setText("⭐ Enregistrer dans Mes Circuits");
                    Toast.makeText(CircuitDetailActivity.this,
                            "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                btnSaveOffline.setEnabled(true);
                btnSaveOffline.setText("⭐ Enregistrer dans Mes Circuits");
                Toast.makeText(CircuitDetailActivity.this,
                        "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ——— Etape RecyclerView Adapter ———

    private static class EtapeAdapter extends RecyclerView.Adapter<EtapeAdapter.VH> {
        private final List<Circuit.Etape> etapes;

        EtapeAdapter(List<Circuit.Etape> etapes) {
            this.etapes = etapes;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_circuit_step, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Circuit.Etape e = etapes.get(pos);
            h.tvNumber.setText(String.valueOf(pos + 1));

            // Time
            String time = e.getHeureVisite();
            h.tvTime.setText(time != null && !time.isBlank() ? time : "—");

            // Duration
            if (e.getDureeMinutes() != null && e.getDureeMinutes() > 0) {
                h.tvDuration.setText(e.getDureeMinutes() + " min");
                h.tvDuration.setVisibility(View.VISIBLE);
            } else {
                h.tvDuration.setVisibility(View.GONE);
            }

            // Place
            if (e.getLieu() != null) {
                h.tvPlaceName.setText(e.getLieu().getNom() != null ? e.getLieu().getNom() : "Lieu");
                String catCity = "";
                if (e.getLieu().getCategorie() != null) catCity += capitalize(e.getLieu().getCategorie());
                if (e.getLieu().getVille() != null)     catCity += (catCity.isEmpty() ? "" : " · ") + e.getLieu().getVille();
                h.tvPlaceCategory.setText(catCity);
            } else {
                h.tvPlaceName.setText("Étape " + (pos + 1));
                h.tvPlaceCategory.setText("");
            }

            // Notes
            String notes = e.getNotes();
            if (notes != null && !notes.isBlank()) {
                h.tvNotes.setText(notes);
                h.tvNotes.setVisibility(View.VISIBLE);
            } else {
                h.tvNotes.setVisibility(View.GONE);
            }

            // Hide connecting line on last item
            h.vLine.setVisibility(pos == etapes.size() - 1 ? View.INVISIBLE : View.VISIBLE);
        }

        private String capitalize(String s) {
            if (s == null || s.isEmpty()) return s;
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        @Override
        public int getItemCount() { return etapes.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvNumber, tvTime, tvDuration, tvPlaceName, tvPlaceCategory, tvNotes;
            View vLine;

            VH(@NonNull View v) {
                super(v);
                tvNumber       = v.findViewById(R.id.tvStepNumber);
                tvTime         = v.findViewById(R.id.tvStepTime);
                tvDuration     = v.findViewById(R.id.tvStepDuration);
                tvPlaceName    = v.findViewById(R.id.tvStepPlaceName);
                tvPlaceCategory = v.findViewById(R.id.tvStepPlaceCategory);
                tvNotes        = v.findViewById(R.id.tvStepNotes);
                vLine          = v.findViewById(R.id.vStepLine);
            }
        }
    }
}
