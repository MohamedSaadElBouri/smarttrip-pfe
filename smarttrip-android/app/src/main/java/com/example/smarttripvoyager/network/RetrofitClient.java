package com.example.smarttripvoyager.network;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Public ngrok URL — works on any network, no USB or Wi-Fi constraint.
    // If the tunnel domain changes, update this line and rebuild the APK.
    private static final String BASE_URL = "https://thirteen-flight-unheated.ngrok-free.dev/api/v1/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            TokenManager tokenManager = new TokenManager(context);
            TokenInterceptor tokenInterceptor = new TokenInterceptor(tokenManager);
            SessionExpiredInterceptor sessionExpiredInterceptor = new SessionExpiredInterceptor(context, tokenManager);

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(tokenInterceptor)
                    .addInterceptor(sessionExpiredInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
}
