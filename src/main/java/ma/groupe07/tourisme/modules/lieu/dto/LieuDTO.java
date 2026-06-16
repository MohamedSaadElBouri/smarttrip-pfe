package ma.groupe07.tourisme.modules.lieu.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LieuDTO {
    private Long id;
    private String nom, categorie, ville, adresse;
    private Double latitude, longitude, noteMoyenne;
    private Integer nombreAvis;
    private String prixEntree, horaires, contact, photoUrl;
    private String storytelling; // dynamically set based on lang param
}
