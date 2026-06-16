package com.example.smarttripvoyager.network;

import com.example.smarttripvoyager.data.model.LoginRequest;
import com.example.smarttripvoyager.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>> login(@Body LoginRequest request);

    @retrofit2.http.GET("auth/me")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.AuthResponse>> getMe();

    @retrofit2.http.GET("utilisateurs/me/stats")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Long>>> getMyStats();

    @retrofit2.http.PUT("utilisateurs/me/update")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.UserDto>> updateMe(@Body java.util.Map<String, String> body);

    @POST("publications/{id}/like")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Object>>> toggleLike(@Path("id") Long id);

    @POST("publications/{id}/sauvegarder")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Boolean>>> toggleSauvegarde(@Path("id") Long id);

    @POST("recommendations/full")
    Call<com.example.smarttripvoyager.data.model.RecommendationResponse> getRecommendations(@Body com.example.smarttripvoyager.data.model.TouristProfileRequest profile);

    @retrofit2.http.GET("publications/feed")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<com.example.smarttripvoyager.data.model.Publication>>> getFeed();

    @retrofit2.http.GET("publications/me")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<com.example.smarttripvoyager.data.model.Publication>>> getMyPosts();

    @retrofit2.http.PUT("utilisateurs/{id}/preferences")
    Call<Void> updatePreferences(@retrofit2.http.Path("id") Long id, @Body com.example.smarttripvoyager.data.model.UpdatePreferencesRequest request);

    @POST("publications")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Publication>> createPublication(@Body com.example.smarttripvoyager.data.model.Publication publication);

    @retrofit2.http.GET("lieux")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.List<com.example.smarttripvoyager.data.model.Lieu>>> getLieux();

    @retrofit2.http.GET("circuits/{id}")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Circuit>> getCircuit(@Path("id") Long id);

    @POST("circuits/{id}/rejoindre")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<Void>> rejoindreCircuit(@Path("id") Long id);

    @retrofit2.http.GET("circuits/mes-circuits")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.List<com.example.smarttripvoyager.data.model.Circuit>>> getMesCircuits();

    @retrofit2.http.DELETE("circuits/mes-circuits/{id}")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<Void>> supprimerMesCircuit(@Path("id") Long id);

    @retrofit2.http.GET("publications/{id}/commentaires")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.List<com.example.smarttripvoyager.data.model.Commentaire>>> getComments(@Path("id") Long id);

    @POST("publications/{id}/commenter")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Commentaire>> addComment(@Path("id") Long id, @Body java.util.Map<String, String> body);

    @POST("interactions")
    Call<com.example.smarttripvoyager.data.model.ApiResponse<Void>> logInteraction(@Body com.example.smarttripvoyager.data.model.InteractionRequest request);
}
