package ma.groupe07.tourisme.modules.formulaire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "questions_formulaire")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionFormulaire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "texte_fr", nullable = false)
    private String texteFr;

    @Column(name = "texte_ar")
    private String texteAr;

    @Column(name = "texte_en")
    private String texteEn;

    @Column(name = "type_question")
    private String typeQuestion; // SINGLE_CHOICE, MULTI_CHOICE, SLIDER, NUMBER, TOGGLE

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "champ_cible")
    private String champCible;

    private Integer ordre;
    private String icone;
}
