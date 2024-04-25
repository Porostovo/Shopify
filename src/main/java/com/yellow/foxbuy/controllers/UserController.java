package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.DTOs.*;
import com.yellow.foxbuy.models.Rating;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final LogService logService;
    private final RatingService ratingService;

    @Autowired
    public UserController(UserService userService,
                          ConfirmationTokenService confirmationTokenService,
                          AuthenticationService authenticationService, LogService logService, RatingService ratingService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.authenticationService = authenticationService;
        this.logService = logService;
        this.ratingService = ratingService;
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
    public ResponseEntity<?> listUsers(@RequestParam (required = false, defaultValue = "1") Integer page) {
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
    @GetMapping("/user/{id}/rating")
    @Operation(summary = "Get ratings of user", description = "Get ratings of specific user. Shows rating id, message, possible response and who gave the rating.")
    @ApiResponse(responseCode = "200", description = "Rating list returned")
    @ApiResponse(responseCode = "400", description = "Error in returning ratings for specific user")
    public ResponseEntity<?> userRatings(@PathVariable UUID id ){
        return ratingService.getUserRatings(id);
    }
    @PostMapping("/user/{id}/rating")
    @Operation(summary = "Post rating to user", description = "Send rating to a user. Need uuid of target user, json body with rating and comment. Returns back the rating sent with id.")
    @ApiResponse(responseCode = "200", description = "Rating has been uploaded")
    @ApiResponse(responseCode = "400", description = "Error in posting the rating")
    public ResponseEntity<?> userRatingPost(@PathVariable UUID id, @Valid @RequestBody RatingDTO ratingDTO,
                                            BindingResult bindingResult, Authentication authentication) throws MessagingException {
        if (bindingResult.hasErrors()) {
            logService.addLog("POST /user/{id}/rating", "ERROR", ratingDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        return ratingService.rateUser(id,
                authentication.getName(), ratingDTO.getComment(), ratingDTO.getRating());
    }
    @DeleteMapping("/user/{id}/rating/{ratingId}")
    @Operation(summary = "Delete one specific rating", description = "Delete rating of uuid user. Need target user uuid and id of rating. Returns back the deleted rating data.")
    @ApiResponse(responseCode = "200", description = "Rating has been deleted")
    @ApiResponse(responseCode = "400", description = "Error in deleting rating")
    public ResponseEntity<?> userRatingDelete(@PathVariable UUID id, @PathVariable Long ratingId,
                                              Authentication authentication){
        return ratingService.deleteComment(id, authentication.getName(), ratingId);
    }
    @PostMapping("/user/rating/{id}")
    @Operation(summary = "Respond to a rating from other user to you", description = "Post response to specific rating. Need only rating id. Only owner of the rating or admin can delete the rating.")
    @ApiResponse(responseCode = "200", description = "Rating has been uploaded")
    @ApiResponse(responseCode = "400", description = "Error in posting the rating")
    public ResponseEntity<?> userRatingResponse(@PathVariable Long id, Authentication authentication,
                                                @Valid @RequestBody RatingResponseDTO ratingResponseDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            logService.addLog("POST /user/rating/{id}", "ERROR", ratingResponseDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        return ratingService.respondToComment(ratingResponseDTO.getReaction(), authentication.getName(), id);
    }
}


