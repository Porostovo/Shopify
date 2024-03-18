package com.yellow.foxbuy.models;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;

}
