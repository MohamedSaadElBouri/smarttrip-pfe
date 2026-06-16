package ma.groupe07.tourisme.modules.auth.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MeResponseDTO {
    private String token;
    private UserSummaryDTO user;
}
