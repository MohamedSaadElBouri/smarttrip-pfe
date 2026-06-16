package ma.groupe07.tourisme.modules.ai.repository;
import ma.groupe07.tourisme.modules.ai.model.SignalComportement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SignalComportementRepository extends JpaRepository<SignalComportement, Long> {
    List<SignalComportement> findByUtilisateurIdAndEntiteType(Long userId, String entiteType);
    long countByEntiteIdAndEntiteType(Long entiteId, String entiteType);
    long countByEntiteIdAndEntiteTypeAndTypeSignal(Long entiteId, String entiteType, String typeSignal);
}
