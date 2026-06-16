package com.example.smarttripvoyager.ui.questionnaire;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.RecommendationResponse;
import com.example.smarttripvoyager.data.model.TouristProfileRequest;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingAiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_ai);

        String jsonRequest = getIntent().getStringExtra("request_json");
        if (jsonRequest != null) {
            TouristProfileRequest request = new Gson().fromJson(jsonRequest, TouristProfileRequest.class);
            performApiCall(request);
        } else {
            finish(); // Cas d'erreur
        }
    }

    private void performApiCall(TouristProfileRequest request) {
        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getRecommendations(request).enqueue(new Callback<RecommendationResponse>() {
            @Override
            public void onResponse(Call<RecommendationResponse> call, Response<RecommendationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(LoadingAiActivity.this, ResultAiActivity.class);
                    intent.putExtra("response_json", new Gson().toJson(response.body()));
                    startActivity(intent);
                    finish();
                } else {
                    android.widget.Toast.makeText(LoadingAiActivity.this, "Erreur du serveur IA. Veuillez réessayer plus tard.", android.widget.Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RecommendationResponse> call, Throwable t) {
                android.widget.Toast.makeText(LoadingAiActivity.this, "Impossible de contacter l'IA: " + t.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
