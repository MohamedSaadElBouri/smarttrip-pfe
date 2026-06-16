package ma.groupe07.tourisme.modules.auth.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.modules.auth.dto.UpdatePreferencesRequest;
import ma.groupe07.tourisme.modules.auth.dto.UserSummaryDTO;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.auth.repository.UtilisateurRepository;
import ma.groupe07.tourisme.modules.publication.repository.LikePublicationRepository;
import ma.groupe07.tourisme.modules.publication.repository.PublicationRepository;
import ma.groupe07.tourisme.modules.publication.repository.SauvegardePublicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;
    private final PublicationRepository pubRepo;
    private final SauvegardePublicationRepository saveRepo;
    private final LikePublicationRepository likeRepo;

    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMyStats(Authentication auth) {
        Long userId = (Long) auth.getCredentials();

        long postsCount = pubRepo.countByUtilisateurId(userId);
        long savedCount = saveRepo.countByUtilisateurId(userId);
        Long likesReceived = likeRepo.countLikesForUserPosts(userId);
        if (likesReceived == null) likesReceived = 0L;

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "postsCount", postsCount,
                "savedCount", savedCount,
                "likesReceived", likesReceived
        )));
    }

    @PutMapping("/me/update")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> updateMe(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (body.containsKey("nom")) user.setNom(body.get("nom"));
        if (body.containsKey("ville")) user.setVille(body.get("ville"));
        if (body.containsKey("pays")) user.setPays(body.get("pays"));
        if (body.containsKey("photoUrl")) user.setPhotoUrl(body.get("photoUrl"));
        utilisateurRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("Profil mis à jour", toSummary(user)));
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> updatePreferences(
            @PathVariable Long id,
            @RequestBody UpdatePreferencesRequest req,
            Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !id.equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this user's preferences");
        }

        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (req.getPreferences() != null) user.setPreferences(req.getPreferences());
        if (req.getLangue() != null) user.setLangue(req.getLangue());
        utilisateurRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("Préférences mises à jour", toSummary(user)));
    }

    private UserSummaryDTO toSummary(Utilisateur user) {
        return UserSummaryDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .role(user.getRole())
                .langue(user.getLangue())
                .pays(user.getPays())
                .ville(user.getVille())
                .preferences(user.getPreferences())
                .photoUrl(user.getPhotoUrl())
                .build();
    }
}
