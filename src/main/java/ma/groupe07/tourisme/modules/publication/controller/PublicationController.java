package ma.groupe07.tourisme.modules.publication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.common.PageResponse;
import ma.groupe07.tourisme.modules.publication.dto.*;
import ma.groupe07.tourisme.modules.publication.service.PublicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService pubService;

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<PageResponse<PublicationDTO>>> getFeed(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        Long viewerId = auth != null ? (Long) auth.getCredentials() : null;
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(pubService.getFeed(region, page, size, viewerId))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PublicationDTO>> create(
            @Valid @RequestBody CreatePublicationRequest req,
            Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Post submitted for review", pubService.create(req, userId)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<PublicationDTO>>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(pubService.getMyPosts(userId, page, size))));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Map<String,Object>>> toggleLike(
            @PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(ApiResponse.success(pubService.toggleLike(id, userId)));
    }

    @PostMapping("/{id}/sauvegarder")
    public ResponseEntity<ApiResponse<Map<String,Boolean>>> toggleSauvegarde(
            @PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        boolean saved = pubService.toggleSauvegarde(id, userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("saved", saved)));
    }

    @PostMapping("/{id}/commenter")
    public ResponseEntity<ApiResponse<CommentaireDTO>> comment(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, String> body,
            Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.status(201)
                .body(ApiResponse.success(pubService.addComment(id, body.get("contenu"), userId)));
    }

    @GetMapping("/{id}/commentaires")
    public ResponseEntity<ApiResponse<List<CommentaireDTO>>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(pubService.getComments(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        pubService.delete(id, userId, isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Post deleted", null));
    }
}
