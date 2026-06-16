package ma.groupe07.tourisme.modules.auth.dto;

import lombok.Data;

@Data
public class UpdatePreferencesRequest {
    private String preferences;
    private String langue;
}
