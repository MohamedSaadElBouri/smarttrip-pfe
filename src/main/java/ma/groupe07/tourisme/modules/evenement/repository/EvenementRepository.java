package ma.groupe07.tourisme.modules.evenement.repository;

import ma.groupe07.tourisme.modules.evenement.model.Evenement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    @Override
    @EntityGraph(attributePaths = "lieu")
    List<Evenement> findAll();

    @EntityGraph(attributePaths = "lieu")
    List<Evenement> findByVille(String ville);

    @Override
    @EntityGraph(attributePaths = "lieu")
    Optional<Evenement> findById(Long id);
}
