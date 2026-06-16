package ma.groupe07.tourisme.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation d'une publication envoyee a /api/rank-feed (Flask).
 * Champs et noms JSON alignes sur Flask_API/AI_API_INTEGRATION_GUIDE.md.
 */
@Data
public class FeedPostRequest {

    @JsonProperty("post_type")
    private String postType;

    private String language;
    private String city;
    private String category;
    private String season;

    @JsonProperty("day_of_week")
    private String dayOfWeek;

    @JsonProperty("hour_posted")
    private int hourPosted;

    @JsonProperty("image_count")
    private int imageCount;

    @JsonProperty("has_video")
    private int hasVideo;

    @JsonProperty("content_length")
    private int contentLength;

    @JsonProperty("is_sponsored")
    private int isSponsored;

    @JsonProperty("poster_total_likes")
    private int posterTotalLikes;

    @JsonProperty("viewer_nationality")
    private String viewerNationality;

    @JsonProperty("viewer_interests")
    private String viewerInterests;

    @JsonProperty("viewer_interest_match")
    private double viewerInterestMatch;

    @JsonProperty("viewer_profile_city")
    private String viewerProfileCity;

    private int likes;
    private int comments;
    private int shares;
    private int saves;
    private int views;

    /** Conserve par Flask dans la reponse pour relier le score a la publication. */
    @JsonProperty("publication_id")
    private Long publicationId;
}
