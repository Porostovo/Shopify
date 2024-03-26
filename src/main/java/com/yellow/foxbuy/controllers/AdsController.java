package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.AdService;
import com.yellow.foxbuy.services.CategoryService;
import com.yellow.foxbuy.services.ErrorsHandling;
import com.yellow.foxbuy.services.UserService;
import com.yellow.foxbuy.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AdsController {
    private final AdService adService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CategoryService categoryService;


    @Autowired
    public AdsController(AdService adService, JwtUtil jwtUtil, UserService userService, CategoryService categoryService) {
        this.adService = adService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.categoryService = categoryService;
    }
    @PostMapping("/advertisement")
    @Operation(summary = "Create Ad", description = "User can create advertisement. Not VIP just 3 ads.")
    @ApiResponse(responseCode = "200", description = "Advertisement was successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> createAd(@Valid @RequestBody AdDTO adDTO, 
                                      BindingResult bindingResult,
                                      Authentication authentication
             ){

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        assert user != null;
        if (user.getAds().size() >= 3) {
            return new ResponseEntity<>("User has 3 advertisements. Get VIP user or delete some ad.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.findCategoryById(adDTO.getCategoryID());

        //Create Ad from AdDTO
        Ad ad = new Ad(adDTO, user, category);

        //Save ad to repository
        try {
            adService.saveAd(ad);
        } catch (Exception e){
            return new ResponseEntity<>("Error saving advertisement: " +e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        //id of the saved ad
        Long id = ad.getId();

        //Return response with ad id
        AdResponseDTO response = new AdResponseDTO();

        response.setId(id);
        response.setTitle(adDTO.getTitle());
        response.setDescription(adDTO.getDescription());
        response.setPrice(adDTO.getPrice());
        response.setZipcode(adDTO.getZipcode());
        response.setCategoryID(adDTO.getCategoryID());

        return ResponseEntity.ok(response);
    }

    @PutMapping("advertisement/{id}")
    @Operation(summary = "Change Ad", description = "User can update just his advertisement.")
    @ApiResponse(responseCode = "200", description = "Advertisement was successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> updateAd(@Valid @PathVariable Long id, @RequestBody AdDTO adDTO,
                                      BindingResult bindingResult,
                                      Authentication authentication){
        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        Optional<Ad> existingAdOptional = adService.findAdById(id);
        Ad existingAd = existingAdOptional.orElse(null);

        if (existingAd == null) {
            return new ResponseEntity<>("Advertisement not found", HttpStatus.NOT_FOUND);
        }

        if (!existingAd.getUser().equals(user)) {
            return new ResponseEntity<>("You are not authorized to update this advertisement", HttpStatus.UNAUTHORIZED);
        }

        try {
            adService.deleteAd(existingAd);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting previous advertisement: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.findCategoryById(adDTO.getCategoryID());

        Ad ad = new Ad(adDTO, user, category);

        try {
            adService.saveAd(ad);

        } catch (Exception e) {
            return new ResponseEntity<>("Error saving advertisement: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //Return response with ad id
        AdResponseDTO response = new AdResponseDTO();

        response.setId(id);
        response.setTitle(adDTO.getTitle());
        response.setDescription(adDTO.getDescription());
        response.setPrice(adDTO.getPrice());
        response.setZipcode(adDTO.getZipcode());
        response.setCategoryID(adDTO.getCategoryID());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("advertisement/{id}")
    @Operation(summary = "Delete Ad", description = "User can delete just his advertisement.")
    @ApiResponse(responseCode = "200", description = "Advertisement was successfully deleted.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user is not verified.")
    public ResponseEntity<?> deleteAd(@PathVariable Long id,
                                      Authentication authentication){

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        Optional<Ad> existingAdOptional = adService.findAdById(id);
        Ad existingAd = existingAdOptional.orElse(null);

        if (existingAd == null) {
            return new ResponseEntity<>("Advertisement not found", HttpStatus.NOT_FOUND);
        }

        if (!existingAd.getUser().equals(user)) {
            return new ResponseEntity<>("You are not authorized to delete this advertisement", HttpStatus.UNAUTHORIZED);
        }
        try {
            adService.deleteAd(existingAd);
            return new ResponseEntity<>("Your ad was deleted", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting previous advertisement: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
