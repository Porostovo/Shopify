package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.AdService;
import com.yellow.foxbuy.services.ErrorsHandling;
import com.yellow.foxbuy.services.UserService;
import com.yellow.foxbuy.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AdsController {
    private final AdService adService;
    private final JwtUtil jwtUtil;
    private final UserService userService;


    @Autowired
    public AdsController(AdService adService, JwtUtil jwtUtil, UserService userService) {
        this.adService = adService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    @PostMapping("/advertisement")
    public ResponseEntity<?> createAd(@Valid @RequestBody AdDTO adDTO, 
                                      BindingResult bindingResult,
                                      Principal principal,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        String username = principal.getName();
        User user = userService.findByUsername(username).orElse(null);

        assert user != null;
        if (user.getAds().size() >= 3) {
            return new ResponseEntity<>("User has 3 advertisements. Get VIP user or delete some ad: ", HttpStatus.BAD_REQUEST);
        }

        //Create Ad from AdDTO
        Ad ad = new Ad(adDTO, user);

        //ad.setCategory(category);

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
}
