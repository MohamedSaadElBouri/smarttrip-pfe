package ma.groupe07.tourisme.modules.publication.model;

import jakarta.persistence.*;
import lombok.*;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "sauvegardes_publication")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SauvegardePublication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @CreationTimestamp
    private LocalDateTime dateSauvegarde;
}
