package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImp implements AuthenticationService {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationServiceImp(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Map<String, String> result = new HashMap<>();

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || password == null) {
            result.put("error", "Field username or field password was empty!");
            return ResponseEntity.status(400).body(result);
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            result.put("error", "Username or password are incorrect.");
            return ResponseEntity.status(400).body(result);
        } else if (!user.isVerified()) {
            result.put("error", "User is not verified.");
            return ResponseEntity.status(400).body(result);
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("error", "Username or password are incorrect.");
            return ResponseEntity.status(400).body(result);
        }

        String token = jwtUtil.createToken(user);
        result.put("message", "Login successful.");
        result.put("token", token);
        return ResponseEntity.ok(result);
    }
}
