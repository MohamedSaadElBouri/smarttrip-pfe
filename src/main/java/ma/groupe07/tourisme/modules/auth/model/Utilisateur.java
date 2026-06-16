package ma.groupe07.tourisme.modules.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "utilisateurs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Builder.Default
    private String role = "TOURISTE"; // TOURISTE or ADMIN

    private String sexe;
    private Integer age;
    private String pays;
    private String ville;

    @Builder.Default
    private String langue = "FR";

    @Column(length = 500)
    private String preferences;

    @Column(name = "photo_url")
    private String photoUrl;

    @Builder.Default
    private Boolean bloque = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
