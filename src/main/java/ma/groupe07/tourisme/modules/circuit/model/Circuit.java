package ma.groupe07.tourisme.modules.circuit.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "circuits")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Circuit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String theme;
    private String ville;

    @Column(name = "duree_jours")
    private Integer dureeJours;

    @Column(name = "prix_estime")
    private Double prixEstime;

    @Column(name = "photo_url")
    private String photoUrl;

    @Builder.Default
    private String statut = "BROUILLON"; // BROUILLON, PUBLIE, ARCHIVE

    @Builder.Default
    @Column(name = "note_moyenne")
    private Double noteMoyenne = 0.0;

    @Builder.Default
    @Column(name = "nombre_avis")
    private Integer nombreAvis = 0;

    @OneToMany(mappedBy = "circuit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("ordre ASC")
    private List<EtapeCircuit> etapes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
