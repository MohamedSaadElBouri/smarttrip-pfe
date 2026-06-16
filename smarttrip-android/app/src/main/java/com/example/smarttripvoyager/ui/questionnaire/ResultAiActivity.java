package com.example.smarttripvoyager.ui.questionnaire;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.RecommendationResponse;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

public class ResultAiActivity extends AppCompatActivity {

    private TextView tvCity;
    private TextView tvExperience;
    private TextView tvTripsCount;
    private MaterialButton btnViewTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_ai);

        tvCity = findViewById(R.id.tvCity);
        tvExperience = findViewById(R.id.tvExperience);
        tvTripsCount = findViewById(R.id.tvTripsCount);
        btnViewTrips = findViewById(R.id.btnViewTrips);

        String jsonResponse = getIntent().getStringExtra("response_json");
        if (jsonResponse != null) {
            RecommendationResponse rec = new Gson().fromJson(jsonResponse, RecommendationResponse.class);
            
            tvCity.setText(rec.getRecommendedCity());
            tvExperience.setText(rec.getRecommendedExperience());
            
            int count = (rec.getTopTrips() != null) ? rec.getTopTrips().size() : 0;
            tvTripsCount.setText(count + " circuits trouvés sur mesure");
        }

        btnViewTrips.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(ResultAiActivity.this, GeneratedTripsActivity.class);
            intent.putExtra("response_json", jsonResponse);
            startActivity(intent);
        });
    }
}
