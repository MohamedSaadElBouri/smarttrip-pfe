package ma.groupe07.tourisme.modules.circuit.repository;

import ma.groupe07.tourisme.modules.circuit.model.EtapeCircuit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EtapeCircuitRepository extends JpaRepository<EtapeCircuit, Long> {
    List<EtapeCircuit> findByCircuitIdOrderByOrdreAsc(Long circuitId);
}
