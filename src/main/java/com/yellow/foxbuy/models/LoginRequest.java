package com.yellow.foxbuy.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {
    @NotBlank(message = "Username is required")
    @NotNull(message = "Username was empty!")
    @NotEmpty(message = "Username was empty!")
    private String username;
    @NotBlank(message = "Password is required")
    @NotNull(message = "Password was empty!")
    @NotEmpty(message = "Password was empty!")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}