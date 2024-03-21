package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.AuthResponseDTO;
import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImp implements AuthenticationService {


    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    @Autowired
    public AuthenticationServiceImp(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<AuthResponseDTO> authenticateUser(LoginRequest loginRequest) {
        AuthResponseDTO response = new AuthResponseDTO();

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || password == null) {
            response.setMessage("Field username or field password was empty!");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            response.setMessage("Username or password are incorrect.");
            return ResponseEntity.badRequest().body(response);
        } else if (!user.getVerified()) {
            response.setMessage("User is not verified.");
            return ResponseEntity.badRequest().body(response);
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            response.setMessage("Username or password are incorrect.");
            return ResponseEntity.badRequest().body(response);
        }

        String token = jwtUtil.createToken(user);
        response.setMessage("Login successful.");
        response.setToken(token);
        return ResponseEntity.ok(response);
    }
}
