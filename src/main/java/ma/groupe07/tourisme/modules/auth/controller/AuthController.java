package ma.groupe07.tourisme.modules.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.modules.auth.dto.*;
import ma.groupe07.tourisme.modules.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(
            @Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Account created successfully", authService.register(req)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(req)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponseDTO>> me(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(authService.getMe(email)));
    }
}
