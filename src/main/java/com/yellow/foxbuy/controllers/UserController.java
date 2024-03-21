package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.DTOs.AuthResponseDTO;
import com.yellow.foxbuy.models.DTOs.LoginRequest;

import com.yellow.foxbuy.models.DTOs.UserDTO;

import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.AuthenticationService;
import com.yellow.foxbuy.services.ConfirmationTokenService;
import com.yellow.foxbuy.services.EmailService;
import com.yellow.foxbuy.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final ConfirmationTokenService confirmationTokenService;

    @Autowired

    public UserController(UserService userService,
                          EmailService emailService,
                          ConfirmationTokenService confirmationTokenService,
                          AuthenticationService authenticationService) {
        this.userService = userService;
        this.emailService = emailService;
        this.confirmationTokenService = confirmationTokenService;
        this.authenticationService = authenticationService;
    }


    @PostMapping("/registration")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserDTO userDTO,
                                              BindingResult bindingResult) throws MessagingException {
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                result.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(400).body(result);
        }

        if (userService.existsByUsername(userDTO.getUsername())) {
            result.put("error", "Username already exists.");
            return ResponseEntity.status(400).body(result);
        } else if (userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Email is already used.");
            return ResponseEntity.status(400).body(result);
        } else {
            User user = new User(userDTO.getUsername(), userDTO.getEmail(), SecurityConfig.passwordEncoder().encode(userDTO.getPassword()));

            String emailVerification = System.getenv("EMAIL_VERIFICATION");

            if (emailVerification.equals("on")) {
                userService.save(user);
                emailService.sendVerificationEmail(user);
                result.put("username", user.getUsername());
                result.put("id", String.valueOf(user.getId()));
                return ResponseEntity.status(200).body(result);
            } else if (emailVerification.isEmpty() || emailVerification.equals("off")) {
                user.setVerified(true);
                userService.save(user);
                result.put("username", user.getUsername());
                result.put("id", String.valueOf(user.getId()));
                return ResponseEntity.status(200).body(result);
            }
            result.put("error", "Invalid value for EMAIL_VERIFICATION (should be 'on' or 'off')");
            return ResponseEntity.status(400).body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLoginAndGenerateJWToken(@Valid @RequestBody LoginRequest loginRequest,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            AuthResponseDTO response = new AuthResponseDTO();
            response.setMessage("Validation failed.");
            return ResponseEntity.badRequest().body(response);
        }
        return authenticationService.authenticateUser(loginRequest);
    }

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }

    @GetMapping(path = "/test")
    public Authentication confirm(Authentication authentication) {
        return authentication;
    }
}


