package ma.groupe07.tourisme.modules.publication.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CommentaireDTO {
    private Long id;
    private String contenu;
    private LocalDateTime date;
    private String auteurNom;
    private String auteurPhotoUrl;
}
