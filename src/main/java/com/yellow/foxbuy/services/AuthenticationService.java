package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.User;
import jakarta.mail.MessagingException;

import java.util.Map;

public interface AuthenticationService {
    Map<String, String> registerUserSuccessful(UserDTO userDTO) throws MessagingException;
    Map<String, String> registerUserFailed(UserDTO userDTO);
    Boolean isRefreshTokenValid(String refreshToken);
    String generateNewJwtToken(User user);
    Map<String, String> loginUserFailed(LoginRequest loginRequest);
    Map<String, String> loginUserSuccessful(LoginRequest loginRequest);
    Map<String, String> verifyJwtTokenFailed(String token);
    Map<String, String> verifyJwtTokenSuccessful(String token);
}
