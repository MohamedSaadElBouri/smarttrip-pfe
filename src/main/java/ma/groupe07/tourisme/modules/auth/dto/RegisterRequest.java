package ma.groupe07.tourisme.modules.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String nom;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String motDePasse;

    @Min(value = 13, message = "Must be at least 13 years old")
    private Integer age;

    private String sexe;
    private String pays;
    private String ville;
    private String langue;
    private String preferences;
    private String photoUrl;
}
