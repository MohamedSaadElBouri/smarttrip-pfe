package ma.groupe07.tourisme.modules.ai.model;

import jakarta.persistence.*;
import lombok.*;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "recommandations_ia")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RecommandationIA {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preferences_utilisees")
    private String preferencesUtilisees;

    @Column(name = "budget_utilise")
    private Double budgetUtilise;

    @Column(name = "duree_utilisee")
    private Integer dureeUtilisee;

    @Column(name = "circuits_recommandes_ids", columnDefinition = "TEXT")
    private String circuitsRecommandesIds;

    @CreationTimestamp
    @Column(name = "date_generation")
    private LocalDateTime dateGeneration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
