package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "Schema used for refresh token")
public class RefreshTokenDTO {
        @NotBlank(message = "Refresh Token is required.")
        @Schema(description = "required", example = "eyJhbGciOiJI...")
        private String refreshToken;
}
