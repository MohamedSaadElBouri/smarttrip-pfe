package ma.groupe07.tourisme.modules.evenement.service;

import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.modules.evenement.model.Evenement;
import ma.groupe07.tourisme.modules.evenement.repository.EvenementRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementService {
    private final EvenementRepository evenementRepo;

    public List<Evenement> findAll() { return evenementRepo.findAll(); }
    public List<Evenement> findByVille(String ville) { return evenementRepo.findByVille(ville); }
    public Evenement findById(Long id) {
        return evenementRepo.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Evenement not found"));
    }
    public Evenement create(Evenement e) { return evenementRepo.save(e); }
    public void delete(Long id) { evenementRepo.deleteById(id); }
}
