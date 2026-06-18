package ma.groupe07.tourisme.modules.publication.repository;

import ma.groupe07.tourisme.modules.publication.model.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findByStatutOrderByDateDesc(String statut, Pageable pageable);
    Page<Publication> findByStatutAndRegionOrderByDateDesc(String statut, String region, Pageable pageable);
    long countByStatut(String statut);
    long countByUtilisateurId(Long utilisateurId);
    Page<Publication> findByUtilisateurIdOrderByDateDesc(Long utilisateurId, Pageable pageable);
    boolean existsByContenu(String contenu);
}
