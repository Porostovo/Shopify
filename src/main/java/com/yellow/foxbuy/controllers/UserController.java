package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.RoleRepository;
import com.yellow.foxbuy.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final ConfirmationTokenService confirmationTokenService;
    private final RoleService roleService;

    @Autowired

    public UserController(UserService userService,
                          EmailService emailService,
                          ConfirmationTokenService confirmationTokenService,
                          AuthenticationService authenticationService,
                          RoleService roleService) {
        this.userService = userService;
        this.emailService = emailService;
        this.confirmationTokenService = confirmationTokenService;
        this.authenticationService = authenticationService;
        this.roleService = roleService;
    }


    @PostMapping("/registration")
    @Operation(summary = "Register a new user", description = "Register a new user with username, email and password.")
    @ApiResponse(responseCode = "200", description = "User created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists.")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserDTO userDTO,
                                              BindingResult bindingResult) throws MessagingException {
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        if (userService.existsByUsername(userDTO.getUsername())) {
            result.put("error", "Username already exists.");
            return ResponseEntity.status(400).body(result);
        } else if (userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Email is already used.");
            return ResponseEntity.status(400).body(result);
        } else {

            Set<Role> userRole = new HashSet<>();
            System.out.println(userService.userRepositoryIsEmpty());
            if (userService.userRepositoryIsEmpty()) {
                userRole.add(roleService.getReferenceById(3L));
            } else {
                userRole.add(roleService.getReferenceById(1L));
            }

            User user = new User(userDTO.getUsername(),
                    userDTO.getEmail(),
                    SecurityConfig.passwordEncoder().encode(userDTO.getPassword()), userRole);

            String emailVerification = System.getenv("EMAIL_VERIFICATION");

            if (emailVerification == null || emailVerification.equals("on")) {
                userService.save(user);
                emailService.sendVerificationEmail(user);
            } else {
                user.setVerified(true);

                userService.save(user);

            }

            result.put("username", user.getUsername());
            result.put("id", String.valueOf(user.getId()));
            return ResponseEntity.status(200).body(result);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "User login with username and password.")
    @ApiResponse(responseCode = "200", description = "User logged successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> userLoginAndGenerateJWToken(@Valid @RequestBody LoginRequest loginRequest,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        return authenticationService.authenticateUser(loginRequest);
    }

    @GetMapping(path = "/confirm")
    @Operation(summary = "Token confirmation", description = "Set user as verified if confirmed.")
    @ApiResponse(responseCode = "200", description = "User set as verified.")
    public String confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
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
            return ResponseEntity.status(400).body(error);
        }
        return ResponseEntity.status(200).body(userService.getDetailsById(id));
    }

    @GetMapping("/user")
    @Operation(summary = "Get list of users", description = "Get username, email, role and number of ads of all the users. Also shows actual page and total pages. Can be used with page parameter.")
    @ApiResponse(responseCode = "200", description = "User list received.")
    @ApiResponse(responseCode = "400", description = "Chosen page is empty.")
    public ResponseEntity<?> listUsers(@RequestParam (required = false, defaultValue = "1") Integer page) {
        int totalPages = userService.getTotalPages(userService.getAllUsers());
        if (page > totalPages) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "This page is empty.");
            return ResponseEntity.status(400).body(error);
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("page", page);
            result.put("total_pages", totalPages);
            result.put("users", userService.listUsersByPage(page));
            return ResponseEntity.status(200).body(result);
        }
    }
}


