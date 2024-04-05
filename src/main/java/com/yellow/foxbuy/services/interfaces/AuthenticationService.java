package com.yellow.foxbuy.services.interfaces;

import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthenticationService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
    ResponseEntity<?> verifyJwtToken(String token);
    Map<String, String> goodRegisterUser(UserDTO userDTO) throws MessagingException;
    Map<String, String> badRegisterUser(UserDTO userDTO);
}
