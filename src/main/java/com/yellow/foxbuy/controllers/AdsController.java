package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.services.implementations.ErrorsHandling;
import com.yellow.foxbuy.services.interfaces.AdManagementService;
import com.yellow.foxbuy.services.interfaces.AdService;
import com.yellow.foxbuy.services.interfaces.CategoryService;
import com.yellow.foxbuy.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
public class AdsController {

    private final AdManagementService adManagementService;
    private final AdService adService;
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public AdsController(AdManagementService adManagementService, AdService adService, CategoryService categoryService, UserService userService) {
        this.adManagementService = adManagementService;
        this.adService = adService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @PostMapping("/advertisement")
    @Operation(summary = "Create Ad", description = "User can create advertisement. Not VIP user only 3 ads.")
    @ApiResponse(responseCode = "200", description = "Advertisement was successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> createAd(@Valid @RequestBody AdDTO adDTO,
                                      BindingResult bindingResult,
                                      Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        return adManagementService.createAd(adDTO, authentication);

    }

    @PutMapping("advertisement/{id}")
    @Operation(summary = "Change Ad", description = "User can update just his advertisement.")
    @ApiResponse(responseCode = "200", description = "Advertisement was successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> updateAd(@Valid @PathVariable Long id, @RequestBody AdDTO adDTO,
                                      BindingResult bindingResult,
                                      Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        return adManagementService.updateAd(id, adDTO, authentication);
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
    public ResponseEntity<?> getAdvertisement(@PathVariable Long id){
        if (!adService.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Ad with this id doesn't exist.");
            return ResponseEntity.status(400).body(error);
        }
        else return ResponseEntity.status(200).body(adService.findById(id));
    }

    @GetMapping("/advertisement")
    @Operation(summary = "Show Ads by user, category (can be used with paging)", description = "User can get list of ads by username or category ID (can be used with paging, where 1 page contains 10 ads).")
    @ApiResponse(responseCode = "200", description = "List of ads  successfully shown.")
    @ApiResponse(responseCode = "400", description = "User or category doesn't exist or unexpected error.")
    public ResponseEntity<?> listAds(@RequestParam (required = false) String user,
                                     @RequestParam (required = false) Long category,
                                     @RequestParam (required = false) Integer page){
        Map<String, String> error = new HashMap<>();
        if (user != null && userService.existsByUsername(user)) {
            return ResponseEntity.status(200).body(adService.findAllByUser(user));
        } else if (user != null && !userService.existsByUsername(user)) {
            error.put("error", "User with this name doesn't exist.");
            return ResponseEntity.status(400).body(error);
        }

        int totalPages = adService.getTotalPages(adService.findAllByCategoryId(category));

        if (page != null && page > totalPages && categoryService.categoryIdExists(category)) {
            error.put("error", "This page is empty.");
            return ResponseEntity.status(400).body(error);
        } else if (!categoryService.categoryIdExists(category)) {
            error.put("error", "Category with this ID doesn't exist.");
            return ResponseEntity.status(400).body(error);
        } else if (page != null && category != null && categoryService.categoryIdExists(category)) {
            Map<String, Object> result = new HashMap<>();
            result.put("page", page);
            result.put("total_pages", totalPages);
            result.put("ads", adService.listAdsByPageAndCategory(page, category));
            return ResponseEntity.status(200).body(result);
        }

        if (category != null && categoryService.categoryIdExists(category)) {
            return ResponseEntity.status(200).body(adService.findAllByCategoryId(category));
        }
        error.put("error", "Unexpected error");
        return ResponseEntity.status(400).body(error);
    }
}
