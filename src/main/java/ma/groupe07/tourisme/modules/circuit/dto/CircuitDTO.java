package ma.groupe07.tourisme.modules.circuit.dto;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CircuitDTO {
    private Long id;
    private String titre, description, theme, ville, photoUrl, statut;
    private Integer dureeJours;
    private Double prixEstime, noteMoyenne;
    private Integer nombreAvis;
    private List<EtapeDTO> etapes;
}
