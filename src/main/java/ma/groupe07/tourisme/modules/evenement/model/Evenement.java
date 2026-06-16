package ma.groupe07.tourisme.modules.evenement.model;

import jakarta.persistence.*;
import lombok.*;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;
import java.time.LocalDateTime;

@Entity @Table(name = "evenements")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Evenement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String categorie;
    private String ville;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Builder.Default
    private Boolean gratuit = true;

    @Column(name = "prix_entree")
    private Double prixEntree;

    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lieu_id")
    private Lieu lieu;
}
