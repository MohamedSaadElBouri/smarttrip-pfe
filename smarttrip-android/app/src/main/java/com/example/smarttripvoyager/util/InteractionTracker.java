package com.example.smarttripvoyager.util;

import android.content.Context;

import com.example.smarttripvoyager.data.model.ApiResponse;
import com.example.smarttripvoyager.data.model.InteractionRequest;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Signale la consultation d'un lieu ou d'un circuit pour enrichir le profil d'engagement utilise par les recommandations IA. */
public final class InteractionTracker {

    private InteractionTracker() {}

    public static void logView(Context context, String entiteType, Long entiteId) {
        if (entiteId == null) return;
        ApiService apiService = RetrofitClient.getApiService(context);
        apiService.logInteraction(new InteractionRequest(entiteType, entiteId, "VIEW")).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {}

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }
}
