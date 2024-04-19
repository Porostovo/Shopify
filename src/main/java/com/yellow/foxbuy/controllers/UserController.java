package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.DTOs.AuthResponseDTO;
import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.RefreshTokenDTO;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.User;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        if (userService.existsByUsername(userDTO.getUsername()) || userService.existsByEmail(userDTO.getEmail())) {
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
                                          BindingResult bindingResult) {
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

    @GetMapping("/user/{id}")
    @Operation(summary = "Get user details by ID", description = "Get username, email, role and list of ads by user UUID.")
    @ApiResponse(responseCode = "200", description = "User details received.")
    @ApiResponse(responseCode = "400", description = "User with this ID doesn't exist.")
    public ResponseEntity<?> getUserDetails(@PathVariable UUID id) throws Exception {
        Map<String, String> error = new HashMap<>();
        if (!userService.existsById(id)) {
            error.put("error", "User doesn't exist.");
            logService.addLog("GET /user/{id}", "ERROR", "id = " + id);
            return ResponseEntity.status(400).body(error);
        }
        logService.addLog("GET /user/{id}", "INFO", "id = " + id);
        return ResponseEntity.status(200).body(userService.getDetailsById(id));
    }

    @GetMapping("/user")
    @Operation(summary = "Get list of users", description = "Get username, email, role and number of ads of all the users. Also shows actual page and total pages. Can be used with page parameter.")
    @ApiResponse(responseCode = "200", description = "User list received.")
    @ApiResponse(responseCode = "400", description = "Chosen page is empty.")
    public ResponseEntity<?> listUsers(@RequestParam(required = false, defaultValue = "1") Integer page) {
        int totalPages = userService.getTotalPages(userService.getAllUsers());
        if (page > totalPages) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "This page is empty.");
            logService.addLog("GET /user", "ERROR", "page = " + page);
            return ResponseEntity.status(400).body(error);
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("page", page);
            result.put("total_pages", totalPages);
            result.put("users", userService.listUsersByPage(page));
            logService.addLog("GET /user", "INFO", "page = " + page);
            return ResponseEntity.status(200).body(result);
        }
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Send refresh token to renew JWT token",
            description = " If JWT token expired, you have to reach this endpoint to renew it.")
    @ApiResponse(responseCode = "200", description = "JWT and refresh token will be returned")
    @ApiResponse(responseCode = "400", description = "Refresh token is not valid. Please make a new sign in request.")
    public ResponseEntity<?> getRefreshToken(@Valid @RequestBody RefreshTokenDTO refreshTOkenDTO,
                                             BindingResult bindingResult) throws MessagingException {
        if (bindingResult.hasErrors()) {
            logService.addLog("POST /refreshToken", "ERROR", refreshTOkenDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        User user = userService.findByRefreshToken(refreshTOkenDTO.getRefreshToken());
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            logService.addLog("POST /refreshToken", "ERROR", refreshTOkenDTO.toString());
            result.put("error", "Refresh token is not in database.");
            return ResponseEntity.status(403).body(result);
        }
        if (!authenticationService.isRefreshTokenValid(refreshTOkenDTO.getRefreshToken())) {
            logService.addLog("POST /refreshToken", "ERROR", refreshTOkenDTO.toString());
            result.put("error", "Refresh token is not valid. Please make a new sign in request.");
            return ResponseEntity.status(403).body(result);
        }
        String jwtToken = authenticationService.generateNewJwtToken(user);
        logService.addLog("POST /registration", "INFO", refreshTOkenDTO.toString());
        result.put("jwtToken", jwtToken);
        result.put("refreshToken", refreshTOkenDTO.getRefreshToken());
        return ResponseEntity.status(200).body(result);
    }
}


