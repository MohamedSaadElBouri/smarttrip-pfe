package ma.groupe07.tourisme.modules.publication.repository;

import ma.groupe07.tourisme.modules.publication.model.LikePublication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface LikePublicationRepository extends JpaRepository<LikePublication, Long> {
    Optional<LikePublication> findByPublicationIdAndUtilisateurId(Long pubId, Long userId);
    long countByPublicationId(Long pubId);

    @Query("SELECT COUNT(l) FROM LikePublication l WHERE l.publication.utilisateur.id = :userId")
    Long countLikesForUserPosts(Long userId);

    long countByUtilisateurId(Long userId);

    @Query("SELECT l FROM LikePublication l JOIN FETCH l.publication WHERE l.utilisateur.id = :userId")
    List<LikePublication> findByUtilisateurIdWithPublication(Long userId);
}
