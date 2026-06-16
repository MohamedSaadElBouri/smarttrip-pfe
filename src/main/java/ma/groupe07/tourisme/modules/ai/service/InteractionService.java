package ma.groupe07.tourisme.modules.ai.service;

import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.modules.ai.dto.InteractionRequest;
import ma.groupe07.tourisme.modules.ai.model.SignalComportement;
import ma.groupe07.tourisme.modules.ai.repository.SignalComportementRepository;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.auth.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class InteractionService {

    private final SignalComportementRepository signalRepo;
    private final UtilisateurRepository userRepo;

    @Transactional
    public void logSignal(InteractionRequest req, Long userId) {
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        signalRepo.save(SignalComportement.builder()
                .typeSignal(req.getTypeSignal())
                .entiteType(req.getEntiteType())
                .entiteId(req.getEntiteId())
                .utilisateur(user)
                .build());
    }
}
