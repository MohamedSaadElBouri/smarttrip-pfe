package ma.groupe07.tourisme.modules.circuit.model;

import jakarta.persistence.*;
import lombok.*;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;

@Entity @Table(name = "etapes_circuit")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EtapeCircuit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ordre;

    @Column(name = "heure_visite")
    private String heureVisite;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circuit_id", nullable = false)
    private Circuit circuit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lieu_id")
    private Lieu lieu;
}
