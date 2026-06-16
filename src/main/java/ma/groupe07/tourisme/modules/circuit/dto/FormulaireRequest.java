package ma.groupe07.tourisme.modules.circuit.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class FormulaireRequest {
    private String preferences;
    private Double budget;
    private Integer dureeJours;
    private Integer nombrePersonnes;
    private String rythme;
    private Boolean accessibilite;
    private String horaire;
    private String hebergement;
}
