package com.example.smarttripvoyager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttripvoyager.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        if (getIntent().getBooleanExtra("session_expired", false)) {
            Toast.makeText(this, "Votre session a expiré, veuillez vous reconnecter", Toast.LENGTH_LONG).show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(LoginActivity.this, "Connexion en cours...", Toast.LENGTH_SHORT).show();

                com.example.smarttripvoyager.network.ApiService apiService = com.example.smarttripvoyager.network.RetrofitClient.getApiService(LoginActivity.this);
                com.example.smarttripvoyager.data.model.LoginRequest loginRequest = new com.example.smarttripvoyager.data.model.LoginRequest(email, password);

                apiService.login(loginRequest).enqueue(new retrofit2.Callback<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>> call, retrofit2.Response<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            String token = response.body().data.token;
                            com.example.smarttripvoyager.network.TokenManager tokenManager = new com.example.smarttripvoyager.network.TokenManager(LoginActivity.this);
                            tokenManager.saveToken(token);
                            if (response.body().data.id != null) {
                                tokenManager.saveUserId(response.body().data.id);
                            }

                            Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, com.example.smarttripvoyager.ui.home.HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}
