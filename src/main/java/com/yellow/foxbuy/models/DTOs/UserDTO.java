package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "Schema used for registration")
public class UserDTO {
    @NotBlank(message = "Username is required.")
    @Schema(description = "required", example = "john")
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    @Schema(description = "required, must include @", example = "john@email.com")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must have at least 8 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,])[A-Za-z\\d@$!%*?&.,]+$", message = "Password must have at least one uppercase, one lowercase, one number, and one special character (@ $ ! % * ? & . ,).")
    @Schema(description = "required, must have at least 8 characters, must include special character (@ $ ! % * ? & . ,)", example = "Password123%")
    private String password;


    public UserDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "username = " + username + " | email = " + email;
    }
}
