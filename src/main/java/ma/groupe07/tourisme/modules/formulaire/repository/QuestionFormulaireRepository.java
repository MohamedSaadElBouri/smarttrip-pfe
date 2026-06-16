package ma.groupe07.tourisme.modules.formulaire.repository;
import ma.groupe07.tourisme.modules.formulaire.model.QuestionFormulaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface QuestionFormulaireRepository extends JpaRepository<QuestionFormulaire, Long> {
    List<QuestionFormulaire> findAllByOrderByOrdreAsc();
}
