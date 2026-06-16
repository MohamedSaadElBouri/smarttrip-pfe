package com.example.smarttripvoyager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.network.TokenManager;
import com.example.smarttripvoyager.ui.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TokenManager tokenManager = new TokenManager(this);
            if (tokenManager.getToken() != null) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000); // 2 secondes d'attente
    }
}
