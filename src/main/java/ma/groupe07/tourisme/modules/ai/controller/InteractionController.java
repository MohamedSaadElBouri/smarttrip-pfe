package ma.groupe07.tourisme.modules.ai.controller;

import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.modules.ai.dto.InteractionRequest;
import ma.groupe07.tourisme.modules.ai.service.InteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Trace les interactions de l'utilisateur (consultation d'un lieu, d'un
 * restaurant, d'un hotel, d'un monument ou d'un circuit) pour enrichir le
 * profil d'engagement utilise par les recommandations IA.
 */
@RestController
@RequestMapping("/api/v1/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> logInteraction(
            @RequestBody InteractionRequest req, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        interactionService.logSignal(req, userId);
        return ResponseEntity.ok(ApiResponse.success("Interaction enregistree", null));
    }
}
