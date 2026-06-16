package ma.groupe07.tourisme.modules.lieu.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.modules.lieu.dto.LieuDTO;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;
import ma.groupe07.tourisme.modules.lieu.repository.LieuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LieuService {

    private final LieuRepository lieuRepo;

    public List<LieuDTO> findAll(String lang) {
        return lieuRepo.findAll().stream()
                .map(l -> toDTO(l, lang))
                .collect(Collectors.toList());
    }

    public LieuDTO findById(Long id, String lang) {
        Lieu lieu = lieuRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lieu not found: " + id));
        return toDTO(lieu, lang);
    }

    public List<LieuDTO> search(String q, String lang) {
        return lieuRepo.findByNomContainingIgnoreCase(q).stream()
                .map(l -> toDTO(l, lang))
                .collect(Collectors.toList());
    }

    public List<LieuDTO> findByVille(String ville, String lang) {
        return lieuRepo.findByVille(ville).stream()
                .map(l -> toDTO(l, lang))
                .collect(Collectors.toList());
    }

    public LieuDTO create(Lieu lieu) {
        return toDTO(lieuRepo.save(lieu), "FR");
    }

    public LieuDTO update(Long id, Lieu updated) {
        Lieu lieu = lieuRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lieu not found: " + id));
        lieu.setNom(updated.getNom());
        lieu.setCategorie(updated.getCategorie());
        lieu.setVille(updated.getVille());
        lieu.setAdresse(updated.getAdresse());
        lieu.setLatitude(updated.getLatitude());
        lieu.setLongitude(updated.getLongitude());
        lieu.setPrixEntree(updated.getPrixEntree());
        lieu.setHoraires(updated.getHoraires());
        lieu.setPhotoUrl(updated.getPhotoUrl());
        lieu.setStorytellingFr(updated.getStorytellingFr());
        lieu.setStorytellingAr(updated.getStorytellingAr());
        lieu.setStorytellingEn(updated.getStorytellingEn());
        return toDTO(lieuRepo.save(lieu), "FR");
    }

    public void delete(Long id) {
        if (!lieuRepo.existsById(id))
            throw new EntityNotFoundException("Lieu not found: " + id);
        lieuRepo.deleteById(id);
    }

    public LieuDTO toDTO(Lieu l, String lang) {
        String storytelling = switch (lang != null ? lang.toUpperCase() : "FR") {
            case "AR" -> l.getStorytellingAr();
            case "EN" -> l.getStorytellingEn();
            default   -> l.getStorytellingFr();
        };
        return LieuDTO.builder()
                .id(l.getId()).nom(l.getNom()).categorie(l.getCategorie())
                .ville(l.getVille()).adresse(l.getAdresse())
                .latitude(l.getLatitude()).longitude(l.getLongitude())
                .noteMoyenne(l.getNoteMoyenne()).nombreAvis(l.getNombreAvis())
                .prixEntree(l.getPrixEntree()).horaires(l.getHoraires())
                .contact(l.getContact()).photoUrl(l.getPhotoUrl())
                .storytelling(storytelling).build();
    }
}
