package ma.groupe07.tourisme.modules.auth.service;

import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.config.JwtUtil;
import ma.groupe07.tourisme.modules.auth.dto.*;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.auth.repository.UtilisateurRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Utilisateur user = Utilisateur.builder()
                .nom(req.getNom())
                .email(req.getEmail())
                .motDePasse(passwordEncoder.encode(req.getMotDePasse()))
                .age(req.getAge())
                .sexe(req.getSexe())
                .pays(req.getPays())
                .ville(req.getVille())
                .langue(req.getLangue() != null ? req.getLangue() : "FR")
                .preferences(req.getPreferences())
                .photoUrl(req.getPhotoUrl())
                .role("TOURISTE")
                .bloque(false)
                .build();

        user = userRepo.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        return AuthResponseDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .role(user.getRole())
                .langue(user.getLangue())
                .token(token)
                .build();
    }

    public AuthResponseDTO login(LoginRequest req) {
        Utilisateur user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (Boolean.TRUE.equals(user.getBloque())) {
            throw new IllegalArgumentException("Account is blocked. Contact support.");
        }

        if (!passwordEncoder.matches(req.getMotDePasse(), user.getMotDePasse())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        return AuthResponseDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .role(user.getRole())
                .langue(user.getLangue())
                .token(token)
                .build();
    }

    public MeResponseDTO getMe(String email) {
        Utilisateur user = userRepo.findByEmail(email)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found"));
        return MeResponseDTO.builder()
                .user(UserSummaryDTO.builder()
                        .id(user.getId())
                        .nom(user.getNom())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .langue(user.getLangue())
                        .pays(user.getPays())
                        .ville(user.getVille())
                        .preferences(user.getPreferences())
                        .photoUrl(user.getPhotoUrl())
                        .build())
                .build();
    }
}
