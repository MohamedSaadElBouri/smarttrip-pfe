package ma.groupe07.tourisme.modules.publication.model;

import jakarta.persistence.*;
import lombok.*;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "publications")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Publication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "photos_supplementaires", columnDefinition = "TEXT")
    private String photosSupplementaires;

    private String region;

    // culture, nature, food, adventure, history, wellness, shopping, festivals
    private String categorie;

    @Builder.Default
    private String statut = "EN_ATTENTE"; // EN_ATTENTE, APPROUVE, REJETE

    @Builder.Default
    @Column(name = "nb_likes")
    private Integer nbLikes = 0;

    @Column(name = "motif_rejet")
    private String motifRejet;

    @CreationTimestamp
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lieu_id")
    private Lieu lieu;
}
