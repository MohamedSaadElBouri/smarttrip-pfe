package ma.groupe07.tourisme.modules.circuit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.config.JwtUtil;
import ma.groupe07.tourisme.modules.circuit.dto.*;
import ma.groupe07.tourisme.modules.circuit.model.Circuit;
import ma.groupe07.tourisme.modules.circuit.service.CircuitService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/circuits")
@RequiredArgsConstructor
public class CircuitController {

    private final CircuitService circuitService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CircuitDTO>>> getAll(
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(ApiResponse.success(circuitService.findAll(statut)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CircuitDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(circuitService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CircuitDTO>> create(@Valid @RequestBody Circuit circuit) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Circuit created", circuitService.create(circuit)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CircuitDTO>> update(
            @PathVariable Long id, @Valid @RequestBody Circuit circuit) {
        return ResponseEntity.ok(ApiResponse.success("Circuit updated", circuitService.update(id, circuit)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        circuitService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Circuit deleted", null));
    }

    @PostMapping("/{id}/rejoindre")
    public ResponseEntity<ApiResponse<Void>> rejoindre(
            @PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        circuitService.rejoindre(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Circuit started!", null));
    }

    @GetMapping("/mes-circuits")
    public ResponseEntity<ApiResponse<List<CircuitDTO>>> getMesCircuits(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(ApiResponse.success(circuitService.getMesCircuits(userId)));
    }

    @DeleteMapping("/mes-circuits/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimerMesCircuit(
            @PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        circuitService.supprimerDeMesCircuits(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Circuit retiré de mes circuits", null));
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<Void>> addReview(
            @Valid @RequestBody ReviewDTO dto, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        circuitService.addReview(dto, userId);
        return ResponseEntity.status(201).body(ApiResponse.success("Review submitted!", null));
    }
}
