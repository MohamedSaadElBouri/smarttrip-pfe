package com.example.smarttripvoyager.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class RecommendationResponse {
    @SerializedName("recommended_city")
    private String recommendedCity;
    
    @SerializedName("recommended_experience")
    private String recommendedExperience;
    
    @SerializedName("top_trips")
    private List<Map<String, Object>> topTrips;

    public String getRecommendedCity() { return recommendedCity; }
    public String getRecommendedExperience() { return recommendedExperience; }
    public List<Map<String, Object>> getTopTrips() { return topTrips; }
}
