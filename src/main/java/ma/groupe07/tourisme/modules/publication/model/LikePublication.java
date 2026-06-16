package ma.groupe07.tourisme.modules.publication.model;

import jakarta.persistence.*;
import lombok.*;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes_publication",
       uniqueConstraints = @UniqueConstraint(columnNames = {"publication_id","utilisateur_id"}))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LikePublication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
}
