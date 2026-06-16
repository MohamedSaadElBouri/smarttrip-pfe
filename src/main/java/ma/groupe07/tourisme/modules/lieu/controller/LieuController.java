package ma.groupe07.tourisme.modules.lieu.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.modules.lieu.dto.LieuDTO;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;
import ma.groupe07.tourisme.modules.lieu.service.LieuService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lieux")
@RequiredArgsConstructor
public class LieuController {

    private final LieuService lieuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LieuDTO>>> getAll(
            @RequestParam(defaultValue = "FR") String lang) {
        return ResponseEntity.ok(ApiResponse.success(lieuService.findAll(lang)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LieuDTO>> getById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "FR") String lang) {
        return ResponseEntity.ok(ApiResponse.success(lieuService.findById(id, lang)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LieuDTO>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "FR") String lang) {
        return ResponseEntity.ok(ApiResponse.success(lieuService.search(q, lang)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LieuDTO>> create(@Valid @RequestBody Lieu lieu) {
        return ResponseEntity.status(201).body(ApiResponse.success("Lieu created", lieuService.create(lieu)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LieuDTO>> update(
            @PathVariable Long id, @Valid @RequestBody Lieu lieu) {
        return ResponseEntity.ok(ApiResponse.success("Lieu updated", lieuService.update(id, lieu)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        lieuService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Lieu deleted", null));
    }
}
