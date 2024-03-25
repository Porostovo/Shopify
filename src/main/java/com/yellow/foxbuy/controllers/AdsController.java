package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.services.AdService;
import com.yellow.foxbuy.services.ErrorsHandling;
import com.yellow.foxbuy.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdsController {
    private final AdService adService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AdsController(AdService adService, JwtUtil jwtUtil) {
        this.adService = adService;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/advertisement")
    public ResponseEntity<?> createAd(@Valid @RequestBody AdDTO adDTO, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
//        //informations from token extraction..need to be fix
//        final String username;
//        final String jwt = null;
//        username = jwtUtil.getUsernameFromJWT(jwt);
        return null;
    }
}
