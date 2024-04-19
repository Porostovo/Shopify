package com.yellow.foxbuy.models.DTOs;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class AuthResponseDTO {
    private String message;

    @NotNull(message = "missing token")
    @NotBlank(message = "missing token")
    private String token;
    private String refreshToken;

    @Override
    public String toString() {
        return "message = " + message + " | token = " + token + " | refreshToken = " + refreshToken;
    }
}