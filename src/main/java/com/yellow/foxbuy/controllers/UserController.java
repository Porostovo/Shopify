package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.config.SecurityConfig;


import com.yellow.foxbuy.models.DTOs.LoginRequest;

import com.yellow.foxbuy.models.DTOs.UserDTO;

import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.ConfirmationTokenService;
import com.yellow.foxbuy.services.EmailService;
import com.yellow.foxbuy.services.UserService;
import com.yellow.foxbuy.utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    private final EmailService emailService;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, EmailService emailService, ConfirmationTokenService confirmationTokenService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.confirmationTokenService = confirmationTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) throws MessagingException {
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                result.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(400).body(result);
        }

        if (userService.existsByUsername(userDTO.getUsername()) && userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Username and email are already used.");
            return ResponseEntity.status(400).body(result);
        } else if (userService.existsByUsername(userDTO.getUsername())) {
            result.put("error", "Username already exists.");
            return ResponseEntity.status(400).body(result);
        } else if (userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Email is already used.");
            return ResponseEntity.status(400).body(result);
        } else if (System.getenv("EMAIL_VERIFICATION").equals("on")) {
            User user = new User(userDTO.getUsername(), userDTO.getEmail(),
                    SecurityConfig.passwordEncoder().encode(userDTO.getPassword()));
            userService.save(user);
            emailService.sendVerificationEmail(user);
            result.put("username", user.getUsername());
            result.put("id", String.valueOf(user.getId()));
            return ResponseEntity.status(200).body(result);
        } else if (System.getenv("EMAIL_VERIFICATION").equals("off")) {
            User user = new User(userDTO.getUsername(), userDTO.getEmail(),
                    SecurityConfig.passwordEncoder().encode(userDTO.getPassword()), true);
            userService.save(user);
            result.put("username", user.getUsername());
            result.put("id", String.valueOf(user.getId()));
            return ResponseEntity.status(200).body(result);
        }
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLoginAndGenerateJWToken(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {

            result.put("error", "Validation failed.");

            for (FieldError error : bindingResult.getFieldErrors()) {
                result.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(400).body(result);
        }

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
        }

        else if (!user.isVerified()){
            result.put("error", "User is not verified.");
            return ResponseEntity.status(400).body(result);
        }

        else if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("error", "Username or password are incorrect.");
            return ResponseEntity.status(400).body(result);
        }

        String token = jwtUtil.createToken(user);
        result.put("message", "Login successful.");
        result.put("token", token);
        return ResponseEntity.ok(result);
    }


    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }
}


