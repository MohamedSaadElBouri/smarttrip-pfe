package ma.groupe07.tourisme.modules.circuit.model;

import jakarta.persistence.*;
import lombok.*;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "adhesions_circuit")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdhesionCircuit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private String statut = "EN_COURS"; // EN_COURS, TERMINE, ABANDONNE

    @Builder.Default
    private Integer progression = 0;

    @Column(name = "etapes_terminees", columnDefinition = "TEXT")
    private String etapesTerminees;

    @CreationTimestamp
    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circuit_id", nullable = false)
    private Circuit circuit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
}
