package ma.groupe07.tourisme.modules.admin.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminStatsDTO {
    private long totalUsers;
    private long totalCircuits;
    private long totalPublications;
    private long pendingPublications;
    private long totalLieux;
    private long totalEvenements;
}
