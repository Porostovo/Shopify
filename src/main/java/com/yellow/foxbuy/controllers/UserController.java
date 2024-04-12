package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.DTOs.AuthResponseDTO;
import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final LogService logService;

    @Autowired
    public UserController(UserService userService,
                          ConfirmationTokenService confirmationTokenService,
                          AuthenticationService authenticationService, LogService logService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.authenticationService = authenticationService;
        this.logService = logService;
    }


    @PostMapping("/registration")
    @Operation(summary = "Register a new user", description = "Register a new user with username, email and password.")
    @ApiResponse(responseCode = "200", description = "User created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists.")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserDTO userDTO,
                                              BindingResult bindingResult) throws MessagingException {
        if (bindingResult.hasErrors()) {
            logService.addLog("POST /registration", "ERROR", userDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        if (userService.existsByUsername(userDTO.getUsername())|| userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.status(400).body(authenticationService.badRegisterUser(userDTO));
        } else {
            logService.addLog("POST /registration", "INFO", userDTO.toString());
            return ResponseEntity.status(200).body(authenticationService.goodRegisterUser(userDTO));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "User login with username and password.")
    @ApiResponse(responseCode = "200", description = "User logged successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> userLoginAndGenerateJWToken(@Valid @RequestBody LoginRequest loginRequest,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logService.addLog("POST /login", "ERROR", loginRequest.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        return authenticationService.authenticateUser(loginRequest);
    }

    @GetMapping(path = "/confirm")
    @Operation(summary = "Token confirmation", description = "Set user as verified if confirmed.")
    @ApiResponse(responseCode = "200", description = "User set as verified.")
    public String confirm(@RequestParam("token") String token) {
        logService.addLog("GET /confirm", "INFO", "token = " + token);
        return confirmationTokenService.confirmToken(token);
    }


    @PostMapping(path = "/identity")
    @Operation(summary = "Check user identity", description = "Check if the JWT token belongs to the user we need")
    @ApiResponse(responseCode = "400", description = "Token is not valid.")
    @ApiResponse(responseCode = "200", description = "Token is verified.")
    public ResponseEntity<?> userIdentity(@Valid @RequestBody AuthResponseDTO authResponseDTO,
                                          BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            logService.addLog("POST /indentity", "ERROR", "token = " + authResponseDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        return authenticationService.verifyJwtToken(authResponseDTO.getToken());
    }

    @GetMapping(path = "/test")
    @Operation(summary = "Endpoint for testing", description = "Testing endpoint.")
    public Authentication confirm(Authentication authentication) {
        return authentication;
    }
}


