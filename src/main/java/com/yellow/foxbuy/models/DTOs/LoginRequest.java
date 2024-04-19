package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "Schema used for login")
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Schema(description = "required", example = "john")
    private String username;
    @NotBlank(message = "Password is required")
    @Schema(description = "required, must have atleast 8 characters, must include special character (@ $ ! % * ? & . ,)", example = "Password123%")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "username = " + username;
    }
}