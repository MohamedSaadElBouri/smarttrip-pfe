package ma.groupe07.tourisme.modules.evenement.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.modules.evenement.model.Evenement;
import ma.groupe07.tourisme.modules.evenement.service.EvenementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evenements")
@RequiredArgsConstructor
public class EvenementController {
    private final EvenementService evenementService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Evenement>>> getAll(
            @RequestParam(required = false) String ville) {
        List<Evenement> list = ville != null
                ? evenementService.findByVille(ville)
                : evenementService.findAll();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Evenement>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(evenementService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Evenement>> create(@Valid @RequestBody Evenement e) {
        return ResponseEntity.status(201).body(ApiResponse.success(evenementService.create(e)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        evenementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted", null));
    }
}
