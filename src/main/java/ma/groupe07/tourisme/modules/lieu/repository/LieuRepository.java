package ma.groupe07.tourisme.modules.lieu.repository;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LieuRepository extends JpaRepository<Lieu, Long> {
    List<Lieu> findByVille(String ville);
    List<Lieu> findByCategorie(String categorie);
    List<Lieu> findByNomContainingIgnoreCase(String nom);
}
