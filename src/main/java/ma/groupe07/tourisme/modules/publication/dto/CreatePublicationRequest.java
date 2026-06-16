package ma.groupe07.tourisme.modules.publication.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreatePublicationRequest {
    @NotBlank(message = "Content is required")
    private String contenu;
    private String photoUrl;
    private String photosSupplementaires;
    private String region;

    @NotBlank(message = "La categorie est requise")
    private String categorie;

    private Long lieuId;
}
