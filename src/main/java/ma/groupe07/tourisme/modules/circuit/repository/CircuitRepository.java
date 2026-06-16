package ma.groupe07.tourisme.modules.circuit.repository;

import ma.groupe07.tourisme.modules.circuit.model.Circuit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CircuitRepository extends JpaRepository<Circuit, Long> {
    List<Circuit> findByStatut(String statut);
    List<Circuit> findByStatutAndVille(String statut, String ville);
    List<Circuit> findByStatutAndThemeContaining(String statut, String theme);
    List<Circuit> findByStatutAndPrixEstimeLessThanEqual(String statut, Double budget);
}
