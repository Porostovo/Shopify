package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.LoginRequest;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/registration")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserDTO userDTO){
        Map<String, String> result = new HashMap<>();
        if (userService.existsByUsername(userDTO.getUsername()) && userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Username and email are already used.");
            return ResponseEntity.status(400).body(result);
        } else if (userService.existsByUsername(userDTO.getUsername())) {
            result.put("error", "Username already exists.");
            return ResponseEntity.status(400).body(result);
        } else if (userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Email is already used.");
            return ResponseEntity.status(400).body(result);
        } else {
            User user = new User(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword());
            userService.save(user);
            result.put("username", user.getUsername());
            result.put("id", String.valueOf(user.getId()));
            return ResponseEntity.status(200).body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLoginAndGenerateToken(@Valid @RequestBody LoginRequest loginRequest){
        return null;
    }


}
