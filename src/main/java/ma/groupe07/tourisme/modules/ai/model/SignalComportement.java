package ma.groupe07.tourisme.modules.ai.model;

import jakarta.persistence.*;
import lombok.*;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "signaux_comportement")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SignalComportement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_signal")
    private String typeSignal; // CLICK, SAVE, COMPLETE, LIKE

    @Column(name = "entite_type")
    private String entiteType; // CIRCUIT, LIEU

    @Column(name = "entite_id")
    private Long entiteId;

    @CreationTimestamp
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
