package com.example.smarttripvoyager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.UpdatePreferencesRequest;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.example.smarttripvoyager.ui.home.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetupActivity extends AppCompatActivity {

    private Spinner spinnerLangue;
    private Spinner spinnerAgeGroup;
    private Spinner spinnerTravelStyle;
    private SwitchMaterial switchLocation;
    private MaterialButton btnFinishSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        spinnerLangue = findViewById(R.id.spinnerLangue);
        spinnerAgeGroup = findViewById(R.id.spinnerAgeGroup);
        spinnerTravelStyle = findViewById(R.id.spinnerTravelStyle);
        switchLocation = findViewById(R.id.switchLocation);
        btnFinishSetup = findViewById(R.id.btnFinishSetup);

        setupSpinners();

        btnFinishSetup.setOnClickListener(v -> savePreferences());
    }

    private void setupSpinners() {
        String[] langues = {"Français (FR)", "English (EN)", "العربية (AR)"};
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, langues);
        spinnerLangue.setAdapter(langAdapter);

        String[] ages = {"18-24", "25-34", "35-44", "45+"};
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ages);
        spinnerAgeGroup.setAdapter(ageAdapter);

        String[] styles = {"cultural", "adventure", "relaxation"};
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, styles);
        spinnerTravelStyle.setAdapter(styleAdapter);
    }

    private void savePreferences() {
        String langue = spinnerLangue.getSelectedItem().toString();
        boolean geo = switchLocation.isChecked();
        String age = spinnerAgeGroup.getSelectedItem().toString();
        String style = spinnerTravelStyle.getSelectedItem().toString();

        JSONObject prefJson = new JSONObject();
        try {
            prefJson.put("age_group", age);
            prefJson.put("travel_style", style);
            // On peut ajouter d'autres champs par défaut pour l'IA
            prefJson.put("budget_level", "mid_range");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UpdatePreferencesRequest request = new UpdatePreferencesRequest(prefJson.toString(), geo, langue);

        ApiService apiService = RetrofitClient.getApiService(this);
        // Supposons que l'ID utilisateur est 1 pour l'instant (à récupérer via TokenManager dans une vraie app)
        Long userId = 1L; 

        apiService.updatePreferences(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SetupActivity.this, "Préférences IA enregistrées !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetupActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(SetupActivity.this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetupActivity.this, HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SetupActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(SetupActivity.this, HomeActivity.class));
                finish();
            }
        });
    }
}
