package com.yellow.foxbuy.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.WatchdogDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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

import static com.yellow.foxbuy.services.AdManagementServiceImp.hasRole;



@RestController
public class AdsController {
    private final AdManagementService adManagementService;
    private final AdService adService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final EmailService emailService;
    private final LogService logService;
    private final WatchdogService watchDogService;

    @Autowired
    public AdsController(AdManagementService adManagementService, AdService adService, CategoryService categoryService, UserService userService, LogService logService, WatchdogService watchDogService, EmailService emailService) {
           this.adManagementService = adManagementService;
        this.adService = adService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.emailService = emailService;
        this.logService = logService;
        this.watchDogService = watchDogService;

    }

    @PostMapping("/advertisement")
    @Operation(summary = "Create Ad", description = "User can create advertisement. Not VIP user only 3 ads.")
    @ApiResponse(responseCode = "200", description = "Advertisement was successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> createAd(@Valid @RequestBody AdDTO adDTO,
                                      BindingResult bindingResult,
                                      Authentication authentication) throws MessagingException {

        if (bindingResult.hasErrors()) {
            logService.addLog("POST /advertisement", "ERROR", adDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        return adManagementService.createAd(adDTO, authentication, new WatchdogDTO());
    }

    @PutMapping("advertisement/{id}")
    @Operation(summary = "Change Ad", description = "User can update just his advertisement.")
    @ApiResponse(responseCode = "200", description = "Advertisement was successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> updateAd(@Valid @PathVariable Long id, @RequestBody AdDTO adDTO,
                                      BindingResult bindingResult,
                                      Authentication authentication) throws MessagingException {

        if (bindingResult.hasErrors()) {
            logService.addLog("PUT /advertisement/{id}", "ERROR", "id = " + id + " | " + adDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        return adManagementService.updateAd(id, adDTO, authentication, new WatchdogDTO());
    }

    @DeleteMapping("advertisement/{id}")
    @Operation(summary = "Delete Ad", description = "User can delete just his advertisement.")
    @ApiResponse(responseCode = "200", description = "Advertisement was successfully deleted.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> deleteAd(@PathVariable Long id,
                                      Authentication authentication) {
        return adManagementService.deleteAd(id, authentication);
    }

    @GetMapping("/advertisement/{id}")
    @Operation(summary = "Get Ad by ID", description = "User can get information about ad by ID.")
    @ApiResponse(responseCode = "200", description = "Ad was found and info is shown.")
    @ApiResponse(responseCode = "400", description = "Ad with this ID doesn't exist.")
    public ResponseEntity<?> getAdvertisement(@PathVariable Long id) {
        if (!adService.existsById(id) || adService.findAdByIdNoOptional(id).isHidden()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Ad with this id doesn't exist.");
            logService.addLog("GET /advertisement/{id}", "ERROR", "id = " + id);
            return ResponseEntity.status(400).body(error);
        } else {
            logService.addLog("GET /advertisement/{id}", "INFO", "id = " + id);
            return ResponseEntity.status(200).body(adService.findById(id));
        }
    }

    @GetMapping("/advertisement")
    @Operation(summary = "Show Ads by user, category (can be used with paging)", description = "User can get list of ads by username or category ID (can be used with paging, where 1 page contains 10 ads).")
    @ApiResponse(responseCode = "200", description = "List of ads  successfully shown.")
    @ApiResponse(responseCode = "400", description = "User or category doesn't exist or unexpected error.")
    public ResponseEntity<?> listAds(@RequestParam(required = false) String user,
                                     @RequestParam(required = false) Long category,
                                     @RequestParam(required = false, defaultValue = "1") Integer page,
                                     @RequestParam(required = false) String search) {
        Map<String, String> error = new HashMap<>();
        if (user != null && userService.existsByUsername(user) && userService.getUserByUsernameNotOptional(user).getBanned() == null) {
            logService.addLog("GET /advertisement", "INFO", "user = " + user);
            return ResponseEntity.status(200).body(adService.findAllByUser(user));
        } else if ((user != null && !userService.existsByUsername(user)) || (user != null && userService.getUserByUsernameNotOptional(user).getBanned() != null)) {
            logService.addLog("GET /advertisement", "ERROR", "user = " + user);
            error.put("error", "User with this name doesn't exist.");
            return ResponseEntity.status(400).body(error);
        }
        if (search != null && !adService.searchAds(search).isEmpty()) {
            logService.addLog("GET /advertisement", "INFO", "search = " + search);
            return ResponseEntity.status(200).body(adService.searchAds(search));
        } else if (search != null && adService.searchAds(search).isEmpty()) {
            error.put("error", "No match found.");
            logService.addLog("GET /advertisement", "ERROR", "search = " + search);
            return ResponseEntity.status(400).body(error);
        }
        int totalPages = adService.getTotalPages(category);

        if (!categoryService.categoryIdExists(category)) {
            error.put("error", "Category with this ID doesn't exist.");
            logService.addLog("GET /advertisement", "ERROR", "category = " + category + " | page = " + page);
            return ResponseEntity.status(400).body(error);
        } else {
            if (page > totalPages) {
                error.put("error", "This page is empty.");
                logService.addLog("GET /advertisement", "ERROR", "category = " + category + " | page = " + page);
                return ResponseEntity.status(400).body(error);
            } else if (category != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("page", page);
                result.put("total_pages", totalPages);
                result.put("ads", adService.listAdsByPageAndCategory(page, category));
                logService.addLog("GET /advertisement", "INFO", "category = " + category + " | page = " + page);
                return ResponseEntity.status(200).body(result);
            }
        }
        error.put("error", "Unexpected error");
        logService.addLog("GET /advertisement", "ERROR", "category = " + category + " | page = " + page);
        return ResponseEntity.status(400).body(error);
    }

    @PostMapping("advertisement/watch")
    @Operation(summary = "Watchdog create", description = "VIP USER can set Watchdog.")
    @ApiResponse(responseCode = "200", description = "Watchdog was successfully created.")
    @ApiResponse(responseCode = "400", description = "An error occurred.")
    public ResponseEntity<?> setUpWatchDog(@Valid @RequestBody WatchdogDTO watchdogDTO, BindingResult bindingResult, Authentication authentication) {
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            logService.addLog("POST /advertisement/watch", "ERROR", watchdogDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);

        if (user == null) {
            result.put("error", "User is not authenticated.");
            logService.addLog("POST /advertisement/watch", "ERROR", watchdogDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
        //set the role VIP
        boolean isVipUser = hasRole(authentication, "ROLE_VIP");
        if (!isVipUser) {
            result.put("error", "User is not VIP and cannot have WATCHDOG.");
            logService.addLog("POST /advertisement/watch", "ERROR", watchdogDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
        // Check if watchdog already exists for the user
        boolean watchdogExists = watchDogService.checkIfWatchdogExists(user, watchdogDTO);
        if (watchdogExists) {
            result.put("error", "This watchdog already exists for this user.");
            logService.addLog("POST /advertisement/watch", "ERROR", watchdogDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
        //calling the method which will create watchdog
        watchDogService.setupWatchdog(watchdogDTO, user, authentication);

        logService.addLog("POST /advertisement/watch", "INFO", watchdogDTO.toString());

        //if the keyword is empty
        String keyword = watchdogDTO.getKeyword();
        if (keyword == null || keyword.isEmpty()) {
            result.put("success", "Watchdog has been set up successfully");

            //if keyword is fill, show the name of the watchdog
        } else {
            result.put("success", "Watchdog '" + keyword + "' has been set up successfully");
        }
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/advertisement/{id}/message")
    @Operation(summary = "Send a message to Seller", description = "User can send a message to Seller.")
    @ApiResponse(responseCode = "200", description = "Thank you for your message.")
    @ApiResponse(responseCode = "400", description = "You cannot write a message to your advertisements.")
    public ResponseEntity<?> sendMessageToSeller(@PathVariable(required = false) Long id,
                                                 @Schema(example = "{\"message\": \"question.\"}")
                                                 @RequestBody(required = false) String requestBody,
                                                 Authentication authentication) throws MessagingException {
        Map<String, String> response = new HashMap<>();
        if (authentication == null){
            response.put("error", "If you want send messages you have to be logged in.");
            return ResponseEntity.status(400).body(response);
        }
        if (id == null || !adService.existsById(id)){
            response.put("error", "You're attempting to write a message to an advertisement that does not exist.");
            return ResponseEntity.status(400).body(response);
        }
        JsonObject jsonObject = new Gson().fromJson(requestBody, JsonObject.class);//ChatGPT :-)
        if (!jsonObject.has("message") || jsonObject.get("message").getAsString().isEmpty()) {
            response.put("error", "Message should have at least 1 letter.");
            return ResponseEntity.status(400).body(response);
        }
        String message = jsonObject.get("message").getAsString();

        if (adManagementService.isMessageToMyself(id, authentication)) {
            response.put("error", "You cannot write a message to your advertisements.");
            return ResponseEntity.status(400).body(response);
        }
        emailService.sendMessageToSeller(authentication, id, message);
        response.put("status", "200");
        response.put("message", "Thank you for your message.");
        return ResponseEntity.status(200).body(response);
    }
}
