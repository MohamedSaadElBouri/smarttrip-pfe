package com.example.smarttripvoyager.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.smarttripvoyager.ui.circuit.CircuitDetailActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTripsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View tvEmpty;
    private SavedCircuitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewMyTrips);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SavedCircuitAdapter(new ArrayList<>(), this::removeCircuit);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedCircuits();
    }

    private void loadSavedCircuits() {
        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getMesCircuits().enqueue(new Callback<ApiResponse<List<Circuit>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Circuit>>> call, Response<ApiResponse<List<Circuit>>> response) {
                List<Circuit> circuits = (response.isSuccessful() && response.body() != null && response.body().data != null)
                        ? response.body().data : new ArrayList<>();
                adapter.setCircuits(circuits);
                updateEmptyState(circuits.isEmpty());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Circuit>>> call, Throwable t) {
                Toast.makeText(MyTripsActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                updateEmptyState(adapter.getItemCount() == 0);
            }
        });
    }

    private void updateEmptyState(boolean empty) {
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void removeCircuit(Circuit circuit) {
        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.supprimerMesCircuit(circuit.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    adapter.removeCircuit(circuit);
                    updateEmptyState(adapter.getItemCount() == 0);
                    Toast.makeText(MyTripsActivity.this, "Circuit retiré de Mes circuits", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyTripsActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(MyTripsActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    interface OnRemoveListener {
        void onRemove(Circuit circuit);
    }

    private static class SavedCircuitAdapter extends RecyclerView.Adapter<SavedCircuitAdapter.ViewHolder> {
        private final List<Circuit> circuits;
        private final OnRemoveListener onRemoveListener;

        SavedCircuitAdapter(List<Circuit> circuits, OnRemoveListener onRemoveListener) {
            this.circuits = circuits;
            this.onRemoveListener = onRemoveListener;
        }

        void setCircuits(List<Circuit> newCircuits) {
            circuits.clear();
            circuits.addAll(newCircuits);
            notifyDataSetChanged();
        }

        void removeCircuit(Circuit circuit) {
            int index = circuits.indexOf(circuit);
            if (index >= 0) {
                circuits.remove(index);
                notifyItemRemoved(index);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_trip, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Circuit circuit = circuits.get(position);
            holder.tvTitle.setText(circuit.getTitre());
            holder.tvDescription.setText(circuit.getDescription());
            holder.tvDuration.setText((circuit.getDureeJours() != null ? circuit.getDureeJours() : 0) + " Jours");
            holder.tvVille.setText(circuit.getVille());

            if (circuit.getPhotoUrl() != null && !circuit.getPhotoUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext()).load(circuit.getPhotoUrl()).centerCrop().into(holder.ivImage);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CircuitDetailActivity.class);
                intent.putExtra("circuit_id", circuit.getId());
                v.getContext().startActivity(intent);
            });

            holder.btnRemove.setOnClickListener(v -> onRemoveListener.onRemove(circuit));
        }

        @Override
        public int getItemCount() {
            return circuits.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription, tvDuration, tvVille;
            ImageView ivImage;
            MaterialButton btnRemove;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTripTitle);
                tvDescription = itemView.findViewById(R.id.tvTripDescription);
                tvDuration = itemView.findViewById(R.id.tvTripDuration);
                tvVille = itemView.findViewById(R.id.tvTripVille);
                ivImage = itemView.findViewById(R.id.ivTripImage);
                btnRemove = itemView.findViewById(R.id.btnRemoveTrip);
            }
        }
    }
}
