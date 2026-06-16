package ma.groupe07.tourisme.modules.publication.repository;

import ma.groupe07.tourisme.modules.publication.model.SauvegardePublication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface SauvegardePublicationRepository extends JpaRepository<SauvegardePublication, Long> {
    Optional<SauvegardePublication> findByPublicationIdAndUtilisateurId(Long publicationId, Long utilisateurId);
    long countByUtilisateurId(Long utilisateurId);
    long countByPublicationId(Long publicationId);

    @Query("SELECT s FROM SauvegardePublication s JOIN FETCH s.publication WHERE s.utilisateur.id = :userId")
    List<SauvegardePublication> findByUtilisateurIdWithPublication(Long userId);
}
