package com.example.smarttripvoyager.ui.exploration;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.Lieu;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogueLieuxActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLieux;
    private ProgressBar progressLieux;
    private LinearLayout layoutLieuxEmpty;
    private LieuAdapter adapter;
    private List<Lieu> allLieux = new ArrayList<>();

    private Chip chipAll, chipMonuments, chipNature, chipResto, chipRestaurants, chipHotels, chipActivites;

    private static final List<String> CATEGORIES_MONUMENTS =
            java.util.Arrays.asList("MONUMENT", "PATRIMOINE", "RUINES", "PLACE", "RELIGION");
    private static final List<String> CATEGORIES_NATURE =
            java.util.Arrays.asList("NATURE");
    private static final List<String> CATEGORIES_ARTISANAT =
            java.util.Arrays.asList("ARTISANAT", "MUSEE");
    private static final List<String> CATEGORIES_RESTAURANTS =
            java.util.Arrays.asList("RESTAURANT");
    private static final List<String> CATEGORIES_HOTELS =
            java.util.Arrays.asList("HOTEL");
    private static final List<String> CATEGORIES_ACTIVITES =
            java.util.Arrays.asList("ACTIVITE");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_lieux);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewLieux = findViewById(R.id.recyclerViewLieux);
        progressLieux = findViewById(R.id.progressLieux);
        layoutLieuxEmpty = findViewById(R.id.layoutLieuxEmpty);
        recyclerViewLieux.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LieuAdapter(this, new ArrayList<>());
        recyclerViewLieux.setAdapter(adapter);

        chipAll = findViewById(R.id.chipAll);
        chipMonuments = findViewById(R.id.chipMonuments);
        chipNature = findViewById(R.id.chipNature);
        chipResto = findViewById(R.id.chipResto);
        chipRestaurants = findViewById(R.id.chipRestaurants);
        chipHotels = findViewById(R.id.chipHotels);
        chipActivites = findViewById(R.id.chipActivites);
        setupFilters();

        loadLieux();
    }

    private void setupFilters() {
        chipAll.setOnClickListener(v -> selectFilter(chipAll, null));
        chipMonuments.setOnClickListener(v -> selectFilter(chipMonuments, CATEGORIES_MONUMENTS));
        chipNature.setOnClickListener(v -> selectFilter(chipNature, CATEGORIES_NATURE));
        chipResto.setOnClickListener(v -> selectFilter(chipResto, CATEGORIES_ARTISANAT));
        chipRestaurants.setOnClickListener(v -> selectFilter(chipRestaurants, CATEGORIES_RESTAURANTS));
        chipHotels.setOnClickListener(v -> selectFilter(chipHotels, CATEGORIES_HOTELS));
        chipActivites.setOnClickListener(v -> selectFilter(chipActivites, CATEGORIES_ACTIVITES));
    }

    private void selectFilter(Chip selected, List<String> categories) {
        chipAll.setChecked(selected == chipAll);
        chipMonuments.setChecked(selected == chipMonuments);
        chipNature.setChecked(selected == chipNature);
        chipResto.setChecked(selected == chipResto);
        chipRestaurants.setChecked(selected == chipRestaurants);
        chipHotels.setChecked(selected == chipHotels);
        chipActivites.setChecked(selected == chipActivites);

        if (categories == null) {
            adapter.setLieux(allLieux);
            updateEmptyState(allLieux.isEmpty());
            return;
        }

        List<Lieu> filtered = new ArrayList<>();
        for (Lieu lieu : allLieux) {
            if (lieu.getCategorie() != null && categories.contains(lieu.getCategorie().toUpperCase())) {
                filtered.add(lieu);
            }
        }
        adapter.setLieux(filtered);
        updateEmptyState(filtered.isEmpty());
    }

    private void updateEmptyState(boolean empty) {
        layoutLieuxEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerViewLieux.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void loadLieux() {
        progressLieux.setVisibility(View.VISIBLE);
        layoutLieuxEmpty.setVisibility(View.GONE);
        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getLieux().enqueue(new Callback<com.example.smarttripvoyager.data.model.ApiResponse<List<Lieu>>>() {
            @Override
            public void onResponse(Call<com.example.smarttripvoyager.data.model.ApiResponse<List<Lieu>>> call, Response<com.example.smarttripvoyager.data.model.ApiResponse<List<Lieu>>> response) {
                progressLieux.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    allLieux = response.body().data;
                    adapter.setLieux(allLieux);
                    updateEmptyState(allLieux.isEmpty());
                } else {
                    Toast.makeText(CatalogueLieuxActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
                    updateEmptyState(allLieux.isEmpty());
                }
            }

            @Override
            public void onFailure(Call<com.example.smarttripvoyager.data.model.ApiResponse<List<Lieu>>> call, Throwable t) {
                progressLieux.setVisibility(View.GONE);
                Toast.makeText(CatalogueLieuxActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                updateEmptyState(allLieux.isEmpty());
            }
        });
    }
}
