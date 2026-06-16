package ma.groupe07.tourisme.modules.circuit.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReviewDTO {
    @NotNull private Long circuitId;
    @Min(1) @Max(5) private Integer note;
    private String contenu;
}
