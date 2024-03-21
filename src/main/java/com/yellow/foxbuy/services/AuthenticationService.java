package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.LoginRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
}
