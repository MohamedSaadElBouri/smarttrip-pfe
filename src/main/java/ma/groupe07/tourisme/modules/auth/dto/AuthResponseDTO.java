package ma.groupe07.tourisme.modules.auth.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponseDTO {
    private Long id;
    private String nom;
    private String email;
    private String role;
    private String langue;
    private String token;
}
