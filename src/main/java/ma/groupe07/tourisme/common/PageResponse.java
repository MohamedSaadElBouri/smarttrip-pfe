package ma.groupe07.tourisme.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Représentation stable et explicite d'une page, utilisée à la place de
 * {@link Page} pour éviter l'avertissement Spring "Serializing PageImpl
 * instances as-is is not supported". Correspond au modèle Page<T> côté Android
 * (content, totalPages, totalElements).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getTotalPages(), page.getTotalElements());
    }
}
