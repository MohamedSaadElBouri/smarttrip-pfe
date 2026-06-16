package ma.groupe07.tourisme.modules.admin.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.modules.admin.dto.AdminStatsDTO;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.auth.repository.UtilisateurRepository;
import ma.groupe07.tourisme.modules.circuit.repository.CircuitRepository;
import ma.groupe07.tourisme.modules.evenement.repository.EvenementRepository;
import ma.groupe07.tourisme.modules.lieu.repository.LieuRepository;
import ma.groupe07.tourisme.modules.publication.dto.PublicationDTO;
import ma.groupe07.tourisme.modules.publication.model.Publication;
import ma.groupe07.tourisme.modules.publication.repository.PublicationRepository;
import ma.groupe07.tourisme.modules.publication.service.PublicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UtilisateurRepository userRepo;
    private final CircuitRepository circuitRepo;
    private final PublicationRepository pubRepo;
    private final LieuRepository lieuRepo;
    private final EvenementRepository evenementRepo;
    private final PublicationService pubService;

    public AdminStatsDTO getStats() {
        return AdminStatsDTO.builder()
                .totalUsers(userRepo.count())
                .totalCircuits(circuitRepo.count())
                .totalPublications(pubRepo.count())
                .pendingPublications(pubRepo.countByStatut("EN_ATTENTE"))
                .totalLieux(lieuRepo.count())
                .totalEvenements(evenementRepo.count())
                .build();
    }

    public Page<PublicationDTO> getPendingPublications(int page, int size) {
        return pubRepo.findByStatutOrderByDateDesc("EN_ATTENTE", PageRequest.of(page, size))
                .map(pubService::toDTO);
    }

    @Transactional
    public void approuverPublication(Long pubId) {
        Publication pub = pubRepo.findById(pubId)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));
        pub.setStatut("APPROUVE");
        pubRepo.save(pub);
    }

    @Transactional
    public void rejeterPublication(Long pubId, String motif) {
        Publication pub = pubRepo.findById(pubId)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));
        pub.setStatut("REJETE");
        pub.setMotifRejet(motif);
        pubRepo.save(pub);
    }

    public List<Utilisateur> getUtilisateurs() {
        return userRepo.findAll();
    }

    @Transactional
    public void toggleBloque(Long userId) {
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setBloque(!Boolean.TRUE.equals(user.getBloque()));
        userRepo.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepo.existsById(userId))
            throw new EntityNotFoundException("User not found");
        userRepo.deleteById(userId);
    }
}
