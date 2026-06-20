package ma.groupe07.tourisme.modules.publication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.groupe07.tourisme.modules.ai.dto.FeedPostRequest;
import ma.groupe07.tourisme.modules.ai.service.AiRecommendationService;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.auth.repository.UtilisateurRepository;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;
import ma.groupe07.tourisme.modules.lieu.repository.LieuRepository;
import ma.groupe07.tourisme.modules.publication.dto.*;
import ma.groupe07.tourisme.modules.publication.model.*;
import ma.groupe07.tourisme.modules.publication.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicationService {

    /** Nombre minimum d'interactions (likes/sauvegardes/commentaires/...) avant que le
     *  classement du feed applique le boost d'engagement fort (0.5x-2.0x). */
    private static final int MIN_INTERACTIONS_FOR_STRONG_RERANK = 4;

    private final PublicationRepository pubRepo;
    private final CommentaireRepository commentRepo;
    private final LikePublicationRepository likeRepo;
    private final SauvegardePublicationRepository saveRepo;
    private final UtilisateurRepository userRepo;
    private final LieuRepository lieuRepo;
    private final AiRecommendationService aiRecommendationService;

    @Transactional(readOnly = true)
    public Page<PublicationDTO> getFeed(String region, int page, int size, Long viewerId) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Publication> pubs = region != null
                ? pubRepo.findByStatutAndRegionOrderByDateDesc("APPROUVE", region, pageable)
                : pubRepo.findByStatutOrderByDateDesc("APPROUVE", pageable);

        List<Publication> content = pubs.getContent();
        if (content.isEmpty()) {
            return pubs.map(p -> toDTO(p, viewerId));
        }

        List<PublicationDTO> dtos = content.stream().map(p -> toDTO(p, viewerId)).collect(Collectors.toList());

        try {
            Utilisateur viewer = viewerId != null ? userRepo.findById(viewerId).orElse(null) : null;
            // Score d'engagement recent (likes/sauvegardes/commentaires/vues) par categorie.
            // Ce calcul utilise uniquement la DB, pas Flask : il fonctionne meme si Flask est DOWN.
            Map<String, Double> engagementByCategory = aiRecommendationService.getCategoryEngagementScores(viewerId);

            List<FeedPostRequest> feedRequests = content.stream()
                    .map(p -> toFeedPostRequest(p, viewer, engagementByCategory))
                    .collect(Collectors.toList());

            // Appel Flask isole : si Flask est indisponible, score neutre (1.0) pour tous les posts.
            // Le boost d'engagement (calcule depuis la DB) s'applique dans tous les cas.
            Map<Long, Double> scores;
            try {
                scores = aiRecommendationService.rankFeed(feedRequests).stream()
                        .collect(Collectors.toMap(
                                m -> ((Number) m.get("publication_id")).longValue(),
                                m -> ((Number) m.get("ai_ranking_score")).doubleValue(),
                                (a, b) -> a));
            } catch (Exception flaskEx) {
                log.warn("Flask rank-feed indisponible, boost d'engagement applique sans score IA");
                scores = content.stream().collect(Collectors.toMap(Publication::getId, p -> 1.0));
            }

            final Map<Long, Double> finalScores = scores;
            dtos.forEach(dto -> dto.setAiRankingScore(finalScores.getOrDefault(dto.getId(), 1.0)));

            // Amplify engagement signal: multiply score by a category engagement boost
            // so that changes in user interactions produce a visible reordering even
            // when Flask is unavailable. Once the user has reached a minimum number of
            // interactions (likes/saves/comments/...), the boost spread widens sharply
            // (0.5x-2.0x) so favourite categories clearly rise and ignored/unliked ones
            // clearly fall; below that threshold a gentle 1.0x-1.5x boost avoids
            // reshuffling the feed on a single stray interaction.
            if (!engagementByCategory.isEmpty()) {
                int totalSignals = aiRecommendationService.getEngagementSignalCount(viewerId);
                double maxEng = engagementByCategory.values().stream().mapToDouble(v -> v).max().orElse(1.0);
                Map<Long, String> pubCategories = content.stream()
                        .collect(Collectors.toMap(Publication::getId,
                                 p -> p.getCategorie() != null ? p.getCategorie() : ""));
                boolean reactive = totalSignals >= MIN_INTERACTIONS_FOR_STRONG_RERANK;
                dtos.forEach(dto -> {
                    String cat = pubCategories.getOrDefault(dto.getId(), "");
                    double eng = engagementByCategory.getOrDefault(cat, 0.0);
                    double ratio = eng / maxEng;
                    double boost = reactive ? (0.5 + 1.5 * ratio) : (1.0 + 0.5 * ratio);
                    if (dto.getAiRankingScore() != null) {
                        dto.setAiRankingScore(dto.getAiRankingScore() * boost);
                    }
                });
            }

            dtos.sort(Comparator.comparing(
                    (PublicationDTO d) -> d.getAiRankingScore() != null ? d.getAiRankingScore() : -1.0)
                    .reversed());

            log.info("Feed recalcule pour viewerId={} : engagement={}, top3={}",
                    viewerId, engagementByCategory,
                    dtos.stream().limit(3).map(d -> d.getId() + "(" + d.getCategorie() + ")").collect(Collectors.toList()));
        } catch (Exception e) {
            log.warn("Erreur classement feed, conservation de l'ordre chronologique", e);
        }

        return new PageImpl<>(dtos, pageable, pubs.getTotalElements());
    }

    @Transactional
    public PublicationDTO create(CreatePublicationRequest req, Long userId) {
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Lieu lieu = req.getLieuId() != null
                ? lieuRepo.findById(req.getLieuId()).orElse(null) : null;

        Publication pub = Publication.builder()
                .contenu(req.getContenu()).photoUrl(req.getPhotoUrl())
                .photosSupplementaires(req.getPhotosSupplementaires())
                .region(req.getRegion()).categorie(req.getCategorie())
                .statut("APPROUVE")
                .utilisateur(user).lieu(lieu).nbLikes(0).build();

        return toDTO(pubRepo.save(pub));
    }

    @Transactional
    public Map<String, Object> toggleLike(Long pubId, Long userId) {
        Publication pub = pubRepo.findById(pubId)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var existingLike = likeRepo.findByPublicationIdAndUtilisateurId(pubId, userId);
        boolean liked;
        if (existingLike.isPresent()) {
            likeRepo.delete(existingLike.get());
            pub.setNbLikes(Math.max(0, pub.getNbLikes() - 1));
            liked = false; // unliked
        } else {
            likeRepo.save(LikePublication.builder().publication(pub).utilisateur(user).build());
            pub.setNbLikes(pub.getNbLikes() + 1);
            liked = true; // liked
        }
        pubRepo.save(pub);
        return Map.of("liked", liked, "likesCount", pub.getNbLikes());
    }

    @Transactional
    public CommentaireDTO addComment(Long pubId, String contenu, Long userId) {
        Publication pub = pubRepo.findById(pubId)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Commentaire c = commentRepo.save(Commentaire.builder()
                .contenu(contenu).publication(pub).utilisateur(user).build());

        return CommentaireDTO.builder()
                .id(c.getId()).contenu(c.getContenu())
                .date(c.getDate()).auteurNom(user.getNom())
                .auteurPhotoUrl(user.getPhotoUrl()).build();
    }

    @Transactional(readOnly = true)
    public List<CommentaireDTO> getComments(Long pubId) {
        return commentRepo.findByPublicationIdOrderByDateAsc(pubId).stream()
                .map(c -> CommentaireDTO.builder()
                        .id(c.getId()).contenu(c.getContenu())
                        .date(c.getDate()).auteurNom(c.getUtilisateur().getNom())
                        .auteurPhotoUrl(c.getUtilisateur().getPhotoUrl()).build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PublicationDTO> getMySavedPublications(Long userId) {
        return saveRepo.findByUtilisateurIdWithPublication(userId).stream()
                .map(s -> toDTO(s.getPublication(), userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PublicationDTO> getMyPosts(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return pubRepo.findByUtilisateurIdOrderByDateDesc(userId, pageable)
                .map(p -> toDTO(p, userId));
    }

    @Transactional
    public boolean toggleSauvegarde(Long pubId, Long userId) {
        Publication pub = pubRepo.findById(pubId)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var existing = saveRepo.findByPublicationIdAndUtilisateurId(pubId, userId);
        if (existing.isPresent()) {
            saveRepo.delete(existing.get());
            return false; // unsaved
        } else {
            saveRepo.save(SauvegardePublication.builder().publication(pub).utilisateur(user).build());
            return true; // saved
        }
    }

    public void delete(Long pubId, Long userId, boolean isAdmin) {
        Publication pub = pubRepo.findById(pubId)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));
        if (!isAdmin && !pub.getUtilisateur().getId().equals(userId))
            throw new IllegalArgumentException("Not authorized to delete this post");
        pubRepo.deleteById(pubId);
    }

    public PublicationDTO toDTO(Publication p) {
        return toDTO(p, null);
    }

    public PublicationDTO toDTO(Publication p, Long viewerId) {
        boolean liked = viewerId != null && likeRepo.findByPublicationIdAndUtilisateurId(p.getId(), viewerId).isPresent();
        boolean saved = viewerId != null && saveRepo.findByPublicationIdAndUtilisateurId(p.getId(), viewerId).isPresent();
        return PublicationDTO.builder()
                .id(p.getId()).contenu(p.getContenu())
                .photoUrl(p.getPhotoUrl())
                .photosSupplementaires(p.getPhotosSupplementaires())
                .region(p.getRegion()).categorie(p.getCategorie())
                .statut(p.getStatut())
                .nbLikes(p.getNbLikes())
                .nbCommentaires((int) commentRepo.countByPublicationId(p.getId()))
                .date(p.getDate())
                .utilisateur(PublicationDTO.AuthorDTO.builder()
                        .id(p.getUtilisateur().getId())
                        .nom(p.getUtilisateur().getNom())
                        .photoUrl(p.getUtilisateur().getPhotoUrl()).build())
                .lieuId(p.getLieu() != null ? p.getLieu().getId() : null)
                .lieuNom(p.getLieu() != null ? p.getLieu().getNom() : null)
                .likedByMe(liked)
                .savedByMe(saved)
                .build();
    }

    /** Construit le post envoye a /api/rank-feed (Flask) a partir d'une publication et du profil du visiteur. */
    private FeedPostRequest toFeedPostRequest(Publication p, Utilisateur viewer, Map<String, Double> engagementByCategory) {
        FeedPostRequest req = new FeedPostRequest();
        req.setPublicationId(p.getId());
        req.setPostType(p.getPhotoUrl() != null && !p.getPhotoUrl().isEmpty() ? "photo" : "text");
        req.setLanguage("fr");
        req.setCity(p.getLieu() != null ? p.getLieu().getVille() : p.getRegion());
        req.setCategory(p.getCategorie());
        req.setSeason(seasonOf(p.getDate()));
        req.setDayOfWeek(p.getDate().getDayOfWeek().toString().toLowerCase());
        req.setHourPosted(p.getDate().getHour());
        req.setImageCount(countImages(p));
        req.setHasVideo(0);
        req.setContentLength(p.getContenu() != null ? p.getContenu().length() : 0);
        req.setIsSponsored(0);
        req.setPosterTotalLikes(p.getNbLikes());
        req.setViewerNationality(viewer != null && viewer.getPays() != null ? viewer.getPays() : "Morocco");
        req.setViewerInterests(viewer != null && viewer.getPreferences() != null ? viewer.getPreferences() : "");
        req.setViewerInterestMatch(interestMatch(p.getCategorie(), viewer, engagementByCategory));
        req.setViewerProfileCity(viewer != null && viewer.getVille() != null ? viewer.getVille() : "Fes");
        int commentCount = (int) commentRepo.countByPublicationId(p.getId());
        int saveCount = (int) saveRepo.countByPublicationId(p.getId());
        req.setLikes(p.getNbLikes());
        req.setComments(commentCount);
        req.setShares(0);
        req.setSaves(saveCount);
        // Pas de tracking de vue individuel par post : estimation a partir de
        // l'engagement reel (likes/commentaires/sauvegardes) plutot qu'une valeur figee.
        req.setViews(p.getNbLikes() * 4 + commentCount * 6 + saveCount * 8);
        return req;
    }

    private String seasonOf(LocalDateTime date) {
        int month = date.getMonthValue();
        if (month >= 3 && month <= 5) return "spring";
        if (month >= 6 && month <= 8) return "summer";
        if (month >= 9 && month <= 11) return "fall";
        return "winter";
    }

    private int countImages(Publication p) {
        int count = (p.getPhotoUrl() != null && !p.getPhotoUrl().isEmpty()) ? 1 : 0;
        if (p.getPhotosSupplementaires() != null && !p.getPhotosSupplementaires().isEmpty()) {
            count += p.getPhotosSupplementaires().split(",").length;
        }
        return count;
    }

    /**
     * Combine la preference declaree (profil) et l'engagement recent reel
     * (likes/sauvegardes/commentaires/vues) pour cette categorie : le plus
     * fort des deux l'emporte, afin que de nouvelles reactions sur une
     * categorie fassent immediatement remonter les posts de cette categorie
     * dans le feed, meme si elle n'est pas dans les preferences declarees.
     */
    private double interestMatch(String categorie, Utilisateur viewer, Map<String, Double> engagementByCategory) {
        if (categorie == null) return 0.0;
        double staticMatch = (viewer != null && viewer.getPreferences() != null
                && viewer.getPreferences().toLowerCase().contains(categorie.toLowerCase())) ? 1.0 : 0.0;
        // Relative scaling: category at max engagement = 1.0, others proportionally lower.
        // Amplifies differences between categories so a shift in user behavior (e.g. more history likes)
        // produces a visible reordering in the feed even when absolute scores are close.
        if (engagementByCategory.isEmpty()) return staticMatch;
        double raw = engagementByCategory.getOrDefault(categorie, 0.0);
        double max = engagementByCategory.values().stream().mapToDouble(v -> v).max().orElse(1.0);
        double relative = max > 0 ? raw / max : 0.0;
        return Math.max(staticMatch, relative);
    }
}
