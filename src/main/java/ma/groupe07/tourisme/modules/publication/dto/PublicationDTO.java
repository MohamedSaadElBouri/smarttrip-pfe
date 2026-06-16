package ma.groupe07.tourisme.modules.publication.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PublicationDTO {
    private Long id;
    private String contenu, photoUrl, photosSupplementaires, region, categorie, statut;
    private Integer nbLikes;
    private Integer nbCommentaires;
    private LocalDateTime date;
    private AuthorDTO utilisateur;
    private Long lieuId;
    private String lieuNom;
    private Double aiRankingScore;
    private boolean likedByMe;
    private boolean savedByMe;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthorDTO {
        private Long id;
        private String nom, photoUrl;
    }
}
