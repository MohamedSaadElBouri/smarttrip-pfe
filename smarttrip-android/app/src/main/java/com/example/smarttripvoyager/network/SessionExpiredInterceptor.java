package com.example.smarttripvoyager.network;

import android.content.Context;
import android.content.Intent;

import com.example.smarttripvoyager.ui.auth.LoginActivity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Interceptor;
import okhttp3.Response;

/** Detecte les reponses 401/403 sans corps (token expire ou invalide) et redirige vers l'ecran de connexion. */
public class SessionExpiredInterceptor implements Interceptor {

    private static final AtomicBoolean redirecting = new AtomicBoolean(false);

    private final Context appContext;
    private final TokenManager tokenManager;

    public SessionExpiredInterceptor(Context context, TokenManager tokenManager) {
        this.appContext = context.getApplicationContext();
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        String path = chain.request().url().encodedPath();
        boolean isAuthEndpoint = path.endsWith("/auth/login") || path.endsWith("/auth/register");

        boolean isAuthError = (response.code() == 401 ||
                (response.code() == 403 && response.body() != null && response.body().contentLength() == 0));

        if (isAuthError && !isAuthEndpoint) {
            if (redirecting.compareAndSet(false, true)) {
                tokenManager.clearToken();
                Intent intent = new Intent(appContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("session_expired", true);
                appContext.startActivity(intent);
            }
        } else if (!isAuthError) {
            redirecting.set(false);
        }

        return response;
    }
}
