package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.ConfirmationTokenService;
import com.yellow.foxbuy.services.EmailService;
import com.yellow.foxbuy.services.EmailServiceImp;
import com.yellow.foxbuy.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final ConfirmationToken confirmationToken;
    private final ConfirmationTokenService confirmationTokenService;

    @Autowired
    public UserController(UserService userService, EmailServiceImp emailServiceImp, EmailService emailService, ConfirmationToken confirmationToken, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.emailService = emailService;
        this.confirmationToken = confirmationToken;
        this.confirmationTokenService = confirmationTokenService;
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

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }
}
