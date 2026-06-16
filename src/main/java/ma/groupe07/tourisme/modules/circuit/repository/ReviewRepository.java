package ma.groupe07.tourisme.modules.circuit.repository;

import ma.groupe07.tourisme.modules.circuit.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCircuitId(Long circuitId);

    @Query("SELECT AVG(r.note) FROM Review r WHERE r.circuit.id = :circuitId")
    Double findAvgNoteByCircuitId(Long circuitId);

    long countByCircuitId(Long circuitId);
}
