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

    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, AuthenticationService authenticationService, EmailService emailService, ConfirmationTokenService confirmationTokenService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
        this.confirmationTokenService = confirmationTokenService;
        this.jwtUtil = jwtUtil;
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

        if (bindingResult.hasErrors()) {
            AuthResponseDTO response = new AuthResponseDTO();
            response.setMessage("Validation failed.");
            return ResponseEntity.badRequest().body(response);
        }

        // Attempt user authentication
        return authenticationService.authenticateUser(loginRequest);
    }

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }
}


