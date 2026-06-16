package com.example.smarttripvoyager.data.model;

import com.google.gson.annotations.SerializedName;

public class TouristProfileRequest {

    public String nationality = "Morocco";
    @SerializedName("age_group") public String ageGroup = "25-34";
    public String gender = "male";
    @SerializedName("origin_climate") public String originClimate = "temperate";
    @SerializedName("budget_level") public String budgetLevel = "mid_range";
    @SerializedName("trip_duration_days") public String tripDurationDays = "3-5";
    @SerializedName("group_type") public String groupType = "couple";
    @SerializedName("group_size") public String groupSize = "2";
    @SerializedName("travel_style") public String travelStyle = "cultural";
    @SerializedName("noise_preference") public String noisePreference = "moderate";
    @SerializedName("activity_level") public String activityLevel = "moderate";
    @SerializedName("food_preference") public String foodPreference = "local_moroccan";

    @SerializedName("interest_history") public int interestHistory = 1;
    @SerializedName("interest_nature") public int interestNature = 1;
    @SerializedName("interest_food") public int interestFood = 1;
    @SerializedName("interest_adventure") public int interestAdventure = 0;
    @SerializedName("interest_photography") public int interestPhotography = 1;
    @SerializedName("interest_wellness") public int interestWellness = 0;
    @SerializedName("interest_shopping") public int interestShopping = 0;
    @SerializedName("interest_festivals") public int interestFestivals = 1;

    @SerializedName("has_visited_before") public int hasVisitedBefore = 0;
    @SerializedName("preferred_season") public String preferredSeason = "spring";
    @SerializedName("accommodation_type") public String accommodationType = "riad";
    @SerializedName("transport_preference") public String transportPreference = "mixed";
    @SerializedName("special_needs") public String specialNeeds = "none";
    
    // Default Engagement
    @SerializedName("total_posts_viewed") public int totalPostsViewed = 10;
    @SerializedName("total_posts_engaged") public int totalPostsEngaged = 5;
    @SerializedName("avg_engagement_score") public double avgEngagementScore = 0.5;
    @SerializedName("top_engaged_category") public String topEngagedCategory = "culture";
    @SerializedName("top_engaged_city") public String topEngagedCity = "Fes";
    @SerializedName("engagement_food") public double engagementFood = 0.5;
    @SerializedName("engagement_culture") public double engagementCulture = 0.8;
    @SerializedName("engagement_nature") public double engagementNature = 0.5;
    @SerializedName("engagement_adventure") public double engagementAdventure = 0.2;
    @SerializedName("engagement_history") public double engagementHistory = 0.7;
    @SerializedName("engagement_wellness") public double engagementWellness = 0.1;
}
