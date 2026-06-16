package ma.groupe07.tourisme.modules.publication.repository;

import ma.groupe07.tourisme.modules.publication.model.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByPublicationIdOrderByDateAsc(Long publicationId);
    long countByPublicationId(Long publicationId);

    @Query("SELECT c FROM Commentaire c JOIN FETCH c.publication WHERE c.utilisateur.id = :userId")
    List<Commentaire> findByUtilisateurIdWithPublication(Long userId);
}
