package ma.groupe07.tourisme.modules.circuit.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EtapeDTO {
    private Long id;
    private Integer ordre;
    private String heureVisite, notes;
    private Integer dureeMinutes;
    private LieuSimpleDTO lieu;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LieuSimpleDTO {
        private Long id;
        private String nom, categorie, ville, photoUrl;
        private Double latitude, longitude;
    }
}
