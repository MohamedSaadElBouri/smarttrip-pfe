package ma.groupe07.tourisme.modules.circuit.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.config.JwtUtil;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.auth.repository.UtilisateurRepository;
import ma.groupe07.tourisme.modules.circuit.dto.*;
import ma.groupe07.tourisme.modules.circuit.model.*;
import ma.groupe07.tourisme.modules.circuit.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CircuitService {

    private final CircuitRepository circuitRepo;
    private final EtapeCircuitRepository etapeRepo;
    private final ReviewRepository reviewRepo;
    private final AdhesionCircuitRepository adhesionRepo;
    private final UtilisateurRepository userRepo;

    public List<CircuitDTO> findAll(String statut) {
        String s = statut != null ? statut : "PUBLIE";
        return circuitRepo.findByStatut(s).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public CircuitDTO findById(Long id) {
        Circuit c = circuitRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Circuit not found: " + id));
        return toDTO(c);
    }

    @Transactional
    public CircuitDTO create(Circuit circuit) {
        circuit.setStatut("BROUILLON");
        return toDTO(circuitRepo.save(circuit));
    }

    @Transactional
    public CircuitDTO update(Long id, Circuit updated) {
        Circuit c = circuitRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Circuit not found: " + id));
        c.setTitre(updated.getTitre());
        c.setDescription(updated.getDescription());
        c.setTheme(updated.getTheme());
        c.setVille(updated.getVille());
        c.setDureeJours(updated.getDureeJours());
        c.setPrixEstime(updated.getPrixEstime());
        c.setPhotoUrl(updated.getPhotoUrl());
        c.setStatut(updated.getStatut());
        return toDTO(circuitRepo.save(c));
    }

    public void delete(Long id) {
        if (!circuitRepo.existsById(id))
            throw new EntityNotFoundException("Circuit not found: " + id);
        circuitRepo.deleteById(id);
    }

    @Transactional
    public void rejoindre(Long circuitId, Long userId) {
        Circuit circuit = circuitRepo.findById(circuitId)
                .orElseThrow(() -> new EntityNotFoundException("Circuit not found"));
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if already active
        boolean alreadyActive = adhesionRepo
                .findByCircuitIdAndUtilisateurIdAndStatut(circuitId, userId, "EN_COURS")
                .isPresent();
        if (alreadyActive)
            throw new IllegalArgumentException("You already have this circuit active");

        adhesionRepo.save(AdhesionCircuit.builder()
                .circuit(circuit).utilisateur(user)
                .statut("EN_COURS").progression(0).build());
    }

    @Transactional(readOnly = true)
    public List<CircuitDTO> getMesCircuits(Long userId) {
        return adhesionRepo.findByUtilisateurId(userId).stream()
                .map(AdhesionCircuit::getCircuit)
                .collect(Collectors.toMap(Circuit::getId, c -> c, (a, b) -> a, java.util.LinkedHashMap::new))
                .values().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void supprimerDeMesCircuits(Long circuitId, Long userId) {
        adhesionRepo.deleteByCircuitIdAndUtilisateurId(circuitId, userId);
    }

    @Transactional
    public void addReview(ReviewDTO dto, Long userId) {
        Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                .orElseThrow(() -> new EntityNotFoundException("Circuit not found"));
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        reviewRepo.save(Review.builder()
                .circuit(circuit).utilisateur(user)
                .note(dto.getNote()).contenu(dto.getContenu()).build());

        // Update average rating
        Double avg = reviewRepo.findAvgNoteByCircuitId(dto.getCircuitId());
        long count = reviewRepo.countByCircuitId(dto.getCircuitId());
        circuit.setNoteMoyenne(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        circuit.setNombreAvis((int) count);
        circuitRepo.save(circuit);
    }

    // Recommendation algorithm: preferences 40% + popularity 30% + budget/duration 30%
    public List<CircuitDTO> recommander(FormulaireRequest req) {
        List<Circuit> circuits = circuitRepo.findByStatut("PUBLIE");

        return circuits.stream()
            .map(c -> {
                double score = 0;

                // 1. Preference match (40%)
                if (req.getPreferences() != null && c.getTheme() != null) {
                    String[] prefs = req.getPreferences().split(",");
                    for (String pref : prefs) {
                        if (c.getTheme().toUpperCase().contains(pref.trim().toUpperCase())) {
                            score += 40.0 / prefs.length;
                        }
                    }
                }

                // 2. Popularity (30%)
                score += Math.min(c.getNoteMoyenne() * 3, 15);
                score += Math.min(c.getNombreAvis() * 0.5, 15);

                // 3. Budget + duration fit (30%)
                if (req.getBudget() != null && c.getPrixEstime() != null) {
                    if (c.getPrixEstime() <= req.getBudget()) score += 15;
                    else score += Math.max(0, 15 - (c.getPrixEstime() - req.getBudget()) / 100);
                }
                if (req.getDureeJours() != null && c.getDureeJours() != null) {
                    if (c.getDureeJours() <= req.getDureeJours()) score += 15;
                    else score += Math.max(0, 15 - (c.getDureeJours() - req.getDureeJours()) * 3);
                }

                return new Object[]{ c, score };
            })
            .sorted((a, b) -> Double.compare((double)b[1], (double)a[1]))
            .limit(3)
            .map(pair -> toDTO((Circuit) pair[0]))
            .collect(Collectors.toList());
    }

    public CircuitDTO toDTO(Circuit c) {
        List<EtapeDTO> etapes = etapeRepo.findByCircuitIdOrderByOrdreAsc(c.getId())
                .stream().map(e -> EtapeDTO.builder()
                        .id(e.getId()).ordre(e.getOrdre())
                        .heureVisite(e.getHeureVisite())
                        .dureeMinutes(e.getDureeMinutes())
                        .notes(e.getNotes())
                        .lieu(e.getLieu() != null ? EtapeDTO.LieuSimpleDTO.builder()
                                .id(e.getLieu().getId()).nom(e.getLieu().getNom())
                                .categorie(e.getLieu().getCategorie())
                                .ville(e.getLieu().getVille())
                                .photoUrl(e.getLieu().getPhotoUrl())
                                .latitude(e.getLieu().getLatitude())
                                .longitude(e.getLieu().getLongitude()).build() : null)
                        .build())
                .collect(Collectors.toList());

        return CircuitDTO.builder()
                .id(c.getId()).titre(c.getTitre()).description(c.getDescription())
                .theme(c.getTheme()).ville(c.getVille()).photoUrl(c.getPhotoUrl())
                .statut(c.getStatut()).dureeJours(c.getDureeJours())
                .prixEstime(c.getPrixEstime()).noteMoyenne(c.getNoteMoyenne())
                .nombreAvis(c.getNombreAvis()).etapes(etapes).build();
    }
}
