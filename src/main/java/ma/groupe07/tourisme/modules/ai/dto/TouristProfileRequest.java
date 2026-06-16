package ma.groupe07.tourisme.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    @JsonProperty("age_group") @JsonAlias("ageGroup")
    private String ageGroup;

    private String gender;

    @JsonProperty("origin_climate") @JsonAlias("originClimate")
    private String originClimate;

    @JsonProperty("budget_level") @JsonAlias("budgetLevel")
    private String budgetLevel;

    @JsonProperty("trip_duration_days") @JsonAlias("tripDurationDays")
    private String tripDurationDays;

    @JsonProperty("group_type") @JsonAlias("groupType")
    private String groupType;

    @JsonProperty("group_size") @JsonAlias("groupSize")
    private String groupSize;

    @JsonProperty("travel_style") @JsonAlias("travelStyle")
    private String travelStyle;

    @JsonProperty("noise_preference") @JsonAlias("noisePreference")
    private String noisePreference;

    @JsonProperty("activity_level") @JsonAlias("activityLevel")
    private String activityLevel;

    @JsonProperty("food_preference") @JsonAlias("foodPreference")
    private String foodPreference;

    @JsonProperty("interest_history") @JsonAlias("interestHistory")
    private int interestHistory;

    @JsonProperty("interest_nature") @JsonAlias("interestNature")
    private int interestNature;

    @JsonProperty("interest_food") @JsonAlias("interestFood")
    private int interestFood;

    @JsonProperty("interest_adventure") @JsonAlias("interestAdventure")
    private int interestAdventure;

    @JsonProperty("interest_photography") @JsonAlias("interestPhotography")
    private int interestPhotography;

    @JsonProperty("interest_wellness") @JsonAlias("interestWellness")
    private int interestWellness;

    @JsonProperty("interest_shopping") @JsonAlias("interestShopping")
    private int interestShopping;

    @JsonProperty("interest_festivals") @JsonAlias("interestFestivals")
    private int interestFestivals;

    @JsonProperty("has_visited_before") @JsonAlias("hasVisitedBefore")
    private int hasVisitedBefore;

    @JsonProperty("preferred_season") @JsonAlias("preferredSeason")
    private String preferredSeason;

    @JsonProperty("accommodation_type") @JsonAlias("accommodationType")
    private String accommodationType;

    @JsonProperty("transport_preference") @JsonAlias("transportPreference")
    private String transportPreference;

    @JsonProperty("special_needs") @JsonAlias("specialNeeds")
    private String specialNeeds;

    @JsonProperty("total_posts_viewed") @JsonAlias("totalPostsViewed")
    private int totalPostsViewed;

    @JsonProperty("total_posts_engaged") @JsonAlias("totalPostsEngaged")
    private int totalPostsEngaged;

    @JsonProperty("avg_engagement_score") @JsonAlias("avgEngagementScore")
    private double avgEngagementScore;

    @JsonProperty("top_engaged_category") @JsonAlias("topEngagedCategory")
    private String topEngagedCategory;

    @JsonProperty("top_engaged_city") @JsonAlias("topEngagedCity")
    private String topEngagedCity;

    @JsonProperty("engagement_food") @JsonAlias("engagementFood")
    private double engagementFood;

    @JsonProperty("engagement_culture") @JsonAlias("engagementCulture")
    private double engagementCulture;

    @JsonProperty("engagement_nature") @JsonAlias("engagementNature")
    private double engagementNature;

    @JsonProperty("engagement_adventure") @JsonAlias("engagementAdventure")
    private double engagementAdventure;

    @JsonProperty("engagement_history") @JsonAlias("engagementHistory")
    private double engagementHistory;

    @JsonProperty("engagement_wellness") @JsonAlias("engagementWellness")
    private double engagementWellness;

    /** Circuits candidats (issus de la DB) a faire scorer par /api/recommend-trips. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("candidate_trips") @JsonAlias("candidateTrips")
    private List<Map<String, Object>> candidateTrips;
}
