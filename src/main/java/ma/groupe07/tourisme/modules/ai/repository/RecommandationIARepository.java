package ma.groupe07.tourisme.modules.ai.repository;
import ma.groupe07.tourisme.modules.ai.model.RecommandationIA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RecommandationIARepository extends JpaRepository<RecommandationIA, Long> {
    List<RecommandationIA> findByUtilisateurIdOrderByDateGenerationDesc(Long userId);
}
