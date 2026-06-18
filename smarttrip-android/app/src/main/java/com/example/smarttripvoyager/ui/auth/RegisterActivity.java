package com.example.smarttripvoyager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.ApiResponse;
import com.example.smarttripvoyager.data.model.AuthResponse;
import com.example.smarttripvoyager.data.model.RegisterRequest;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.example.smarttripvoyager.network.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8) {
                Toast.makeText(RegisterActivity.this, "Le mot de passe doit contenir au moins 8 caractères", Toast.LENGTH_SHORT).show();
                return;
            }

            register(name, email, password);
        });
    }

    private void register(String name, String email, String password) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Inscription en cours...");

        ApiService apiService = RetrofitClient.getApiService(this);
        RegisterRequest request = new RegisterRequest(name, email, password);

        apiService.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call,
                                    @NonNull Response<ApiResponse<AuthResponse>> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("S'inscrire");

                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    AuthResponse auth = response.body().data;
                    TokenManager tokenManager = new TokenManager(RegisterActivity.this);
                    tokenManager.saveToken(auth.token);
                    if (auth.id != null) {
                        tokenManager.saveUserId(auth.id);
                    }

                    Toast.makeText(RegisterActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, SetupActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, extractErrorMessage(response), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("S'inscrire");
                Toast.makeText(RegisterActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Extrait le message d'erreur du corps de reponse JSON ({success,message,data}) renvoye par le backend. */
    private String extractErrorMessage(Response<ApiResponse<AuthResponse>> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                if (errorResponse != null && errorResponse.message != null) {
                    if ("Validation failed".equals(errorResponse.message) && errorResponse.data instanceof java.util.Map) {
                        java.util.Map<?, ?> fieldErrors = (java.util.Map<?, ?>) errorResponse.data;
                        if (!fieldErrors.isEmpty()) {
                            Object firstError = fieldErrors.values().iterator().next();
                            return String.valueOf(firstError);
                        }
                    }
                    return errorResponse.message;
                }
            }
        } catch (Exception ignored) {
            // Reponse non parsable : on retombe sur le message generique ci-dessous.
        }
        return "Erreur lors de l'inscription (code " + response.code() + ")";
    }
}
