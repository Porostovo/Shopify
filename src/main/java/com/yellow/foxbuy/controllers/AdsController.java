package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.services.AdService;
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

    @Autowired
    public AdsController(AdService adService) {
        this.adService = adService;
    }
    @PostMapping("/advertisement")
    public ResponseEntity<?> createAd(@Valid @RequestBody AdDTO adDTO, BindingResult bindingResult){

        return null;
    }
}
