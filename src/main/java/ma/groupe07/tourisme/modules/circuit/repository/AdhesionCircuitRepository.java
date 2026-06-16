package ma.groupe07.tourisme.modules.circuit.repository;

import ma.groupe07.tourisme.modules.circuit.model.AdhesionCircuit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AdhesionCircuitRepository extends JpaRepository<AdhesionCircuit, Long> {
    List<AdhesionCircuit> findByUtilisateurId(Long userId);
    Optional<AdhesionCircuit> findByCircuitIdAndUtilisateurIdAndStatut(Long circuitId, Long userId, String statut);
    void deleteByCircuitIdAndUtilisateurId(Long circuitId, Long userId);
}
