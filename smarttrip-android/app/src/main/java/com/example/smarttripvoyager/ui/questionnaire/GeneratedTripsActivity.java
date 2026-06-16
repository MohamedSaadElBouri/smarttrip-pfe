package com.example.smarttripvoyager.ui.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.RecommendationResponse;
import com.example.smarttripvoyager.ui.circuit.CircuitDetailActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class GeneratedTripsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTrips;
    private LinearLayout layoutTripsEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_trips);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewTrips = findViewById(R.id.recyclerViewTrips);
        recyclerViewTrips.setLayoutManager(new LinearLayoutManager(this));
        layoutTripsEmpty = findViewById(R.id.layoutTripsEmpty);

        String jsonResponse = getIntent().getStringExtra("response_json");
        List<Map<String, Object>> topTrips = null;
        if (jsonResponse != null) {
            RecommendationResponse rec = new Gson().fromJson(jsonResponse, RecommendationResponse.class);
            topTrips = rec.getTopTrips();
        }

        if (topTrips != null && !topTrips.isEmpty()) {
            recyclerViewTrips.setAdapter(new TripAdapter(topTrips));
            layoutTripsEmpty.setVisibility(View.GONE);
            recyclerViewTrips.setVisibility(View.VISIBLE);
        } else {
            layoutTripsEmpty.setVisibility(View.VISIBLE);
            recyclerViewTrips.setVisibility(View.GONE);
        }
    }

    private static class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
        private final List<Map<String, Object>> trips;

        public TripAdapter(List<Map<String, Object>> trips) {
            this.trips = trips;
        }

        @NonNull
        @Override
        public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_ia, parent, false);
            return new TripViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
            Map<String, Object> trip = trips.get(position);
            Object titleObj = trip.get("trip_title");
            if (titleObj == null || "null".equals(String.valueOf(titleObj))) titleObj = trip.get("trip_name");
            holder.tvTitle.setText(titleObj != null && !"null".equals(String.valueOf(titleObj))
                    ? String.valueOf(titleObj) : "Circuit personnalisé");

            Object descObj = trip.get("trip_description");
            holder.tvDesc.setText(descObj != null && !"null".equals(String.valueOf(descObj))
                    ? String.valueOf(descObj) : "");

            Object duration = trip.get("duration_days");
            if (duration instanceof Double) duration = ((Double) duration).intValue();
            holder.tvDuration.setText(duration != null && !"null".equals(String.valueOf(duration))
                    ? duration + " Jours" : "— Jours");

            Object budget = trip.get("estimated_budget");
            if (budget instanceof Double && ((Double) budget) == Math.floor((Double) budget)) {
                holder.tvBudget.setText(((Double) budget).intValue() + " MAD");
            } else {
                holder.tvBudget.setText(budget != null && !"null".equals(String.valueOf(budget))
                        ? budget + " MAD" : "— MAD");
            }

            Object reason = trip.get("reason");
            if (reason != null && !String.valueOf(reason).isBlank()) {
                holder.tvReason.setText(String.valueOf(reason));
                holder.layoutReason.setVisibility(View.VISIBLE);
            } else {
                holder.layoutReason.setVisibility(View.GONE);
            }

            holder.chipGroupCategories.removeAllViews();
            Object categories = trip.get("matched_categories");
            if (categories instanceof List) {
                for (Object cat : (List<?>) categories) {
                    if (cat == null) continue;
                    Chip chip = new Chip(holder.chipGroupCategories.getContext());
                    chip.setText(String.valueOf(cat));
                    chip.setCheckable(false);
                    chip.setClickable(false);
                    chip.setChipBackgroundColorResource(R.color.primary_container);
                    chip.setTextColor(androidx.core.content.ContextCompat.getColor(
                            holder.chipGroupCategories.getContext(), R.color.on_primary_container));
                    chip.setTextSize(11f);
                    holder.chipGroupCategories.addView(chip);
                }
            }
            holder.chipGroupCategories.setVisibility(
                    holder.chipGroupCategories.getChildCount() > 0 ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                Object tripId = trip.get("trip_id");
                if (tripId != null) {
                    Intent intent = new Intent(v.getContext(), CircuitDetailActivity.class);
                    intent.putExtra("circuit_id", ((Number) tripId).longValue());
                    Object r = trip.get("reason");
                    if (r != null && !String.valueOf(r).isBlank()) {
                        intent.putExtra("ai_reason", String.valueOf(r));
                    }
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "Détails non disponibles pour ce circuit", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return trips != null ? trips.size() : 0;
        }

        static class TripViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDesc, tvDuration, tvBudget, tvReason;
            LinearLayout layoutReason;
            ChipGroup chipGroupCategories;
            public TripViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTripTitle);
                tvDesc = itemView.findViewById(R.id.tvTripDescription);
                tvDuration = itemView.findViewById(R.id.tvTripDuration);
                tvBudget = itemView.findViewById(R.id.tvTripBudget);
                tvReason = itemView.findViewById(R.id.tvTripReason);
                layoutReason = itemView.findViewById(R.id.layoutReason);
                chipGroupCategories = itemView.findViewById(R.id.chipGroupCategories);
            }
        }
    }
}
