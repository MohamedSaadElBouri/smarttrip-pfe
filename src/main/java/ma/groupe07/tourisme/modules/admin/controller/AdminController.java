package ma.groupe07.tourisme.modules.admin.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.modules.admin.dto.*;
import ma.groupe07.tourisme.modules.admin.service.AdminService;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.publication.dto.PublicationDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsDTO>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getStats()));
    }

    @GetMapping("/publications/pending")
    public ResponseEntity<ApiResponse<Page<PublicationDTO>>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getPendingPublications(page, size)));
    }

    @PostMapping("/publications/{id}/approuver")
    public ResponseEntity<ApiResponse<Void>> approuver(@PathVariable Long id) {
        adminService.approuverPublication(id);
        return ResponseEntity.ok(ApiResponse.success("Publication approved", null));
    }

    @PostMapping("/publications/{id}/rejeter")
    public ResponseEntity<ApiResponse<Void>> rejeter(
            @PathVariable Long id, @Valid @RequestBody RejectRequest req) {
        adminService.rejeterPublication(id, req.getMotifRejet());
        return ResponseEntity.ok(ApiResponse.success("Publication rejected", null));
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<ApiResponse<List<Utilisateur>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUtilisateurs()));
    }

    @PutMapping("/utilisateurs/{id}/bloquer")
    public ResponseEntity<ApiResponse<Void>> toggleBloque(@PathVariable Long id) {
        adminService.toggleBloque(id);
        return ResponseEntity.ok(ApiResponse.success("User status updated", null));
    }

    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}
