package ma.groupe07.tourisme.modules.lieu.model;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Entity @Table(name = "lieux")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Lieu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    private String categorie;
    private String ville;
    private String adresse;
    private Double latitude;
    private Double longitude;

    @Column(name = "photo_url")
    private String photoUrl;

    @Builder.Default
    @Column(name = "note_moyenne")
    private Double noteMoyenne = 0.0;

    @Builder.Default
    @Column(name = "nombre_avis")
    private Integer nombreAvis = 0;

    @Column(name = "prix_entree")
    private String prixEntree;

    private String horaires;
    private String contact;

    @Column(name = "storytelling_fr", columnDefinition = "TEXT")
    private String storytellingFr;

    @Column(name = "storytelling_ar", columnDefinition = "TEXT")
    private String storytellingAr;

    @Column(name = "storytelling_en", columnDefinition = "TEXT")
    private String storytellingEn;
}
