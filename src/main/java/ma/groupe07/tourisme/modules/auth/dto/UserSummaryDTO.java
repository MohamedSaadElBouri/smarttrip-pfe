package ma.groupe07.tourisme.modules.auth.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String nom;
    private String email;
    private String role;
    private String langue;
    private String pays;
    private String ville;
    private String preferences;
    private String photoUrl;
}
