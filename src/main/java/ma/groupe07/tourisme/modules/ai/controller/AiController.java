package ma.groupe07.tourisme.modules.ai.controller;

import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.modules.ai.dto.TouristProfileRequest;
import ma.groupe07.tourisme.modules.ai.service.AiRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class AiController {

    private final AiRecommendationService aiRecommendationService;

    /**
     * Reponse non encapsulee dans ApiResponse : l'app Android (RecommendationResponse)
     * attend directement {recommended_city, recommended_experience, top_trips} a la racine.
     */
    @PostMapping("/full")
    public ResponseEntity<Map<String, Object>> getFullRecommendation(
            @RequestBody TouristProfileRequest profile, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(aiRecommendationService.getFullRecommendation(profile, userId));
    }
}
