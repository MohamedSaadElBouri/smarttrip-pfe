package ma.groupe07.tourisme.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Profil touriste echange avec l'API IA Flask (/api/recommend-city,
 * /api/recommend-experience, /api/recommend-trips). Les noms JSON snake_case
 * correspondent exactement au modele TouristProfileRequest cote Android et
 * au contrat documente dans Flask_API/AI_API_INTEGRATION_GUIDE.md.
 */
@Data
public class TouristProfileRequest {

    private String nationality;

    @JsonProperty("age_group")
    private String ageGroup;

    private String gender;

    @JsonProperty("origin_climate")
    private String originClimate;

    @JsonProperty("budget_level")
    private String budgetLevel;

    @JsonProperty("trip_duration_days")
    private String tripDurationDays;

    @JsonProperty("group_type")
    private String groupType;

    @JsonProperty("group_size")
    private String groupSize;

    @JsonProperty("travel_style")
    private String travelStyle;

    @JsonProperty("noise_preference")
    private String noisePreference;

    @JsonProperty("activity_level")
    private String activityLevel;

    @JsonProperty("food_preference")
    private String foodPreference;

    @JsonProperty("interest_history")
    private int interestHistory;

    @JsonProperty("interest_nature")
    private int interestNature;

    @JsonProperty("interest_food")
    private int interestFood;

    @JsonProperty("interest_adventure")
    private int interestAdventure;

    @JsonProperty("interest_photography")
    private int interestPhotography;

    @JsonProperty("interest_wellness")
    private int interestWellness;

    @JsonProperty("interest_shopping")
    private int interestShopping;

    @JsonProperty("interest_festivals")
    private int interestFestivals;

    @JsonProperty("has_visited_before")
    private int hasVisitedBefore;

    @JsonProperty("preferred_season")
    private String preferredSeason;

    @JsonProperty("accommodation_type")
    private String accommodationType;

    @JsonProperty("transport_preference")
    private String transportPreference;

    @JsonProperty("special_needs")
    private String specialNeeds;

    @JsonProperty("total_posts_viewed")
    private int totalPostsViewed;

    @JsonProperty("total_posts_engaged")
    private int totalPostsEngaged;

    @JsonProperty("avg_engagement_score")
    private double avgEngagementScore;

    @JsonProperty("top_engaged_category")
    private String topEngagedCategory;

    @JsonProperty("top_engaged_city")
    private String topEngagedCity;

    @JsonProperty("engagement_food")
    private double engagementFood;

    @JsonProperty("engagement_culture")
    private double engagementCulture;

    @JsonProperty("engagement_nature")
    private double engagementNature;

    @JsonProperty("engagement_adventure")
    private double engagementAdventure;

    @JsonProperty("engagement_history")
    private double engagementHistory;

    @JsonProperty("engagement_wellness")
    private double engagementWellness;

    /** Circuits candidats (issus de la DB) a faire scorer par /api/recommend-trips. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("candidate_trips")
    private List<Map<String, Object>> candidateTrips;
}
