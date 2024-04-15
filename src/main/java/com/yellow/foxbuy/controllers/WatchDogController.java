package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.DTOs.WatchDogDTO;
import com.yellow.foxbuy.services.WatchDogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatchDogController {

    private final WatchDogService watchDogService;

    public WatchDogController(WatchDogService watchDogService) {
        this.watchDogService = watchDogService;
    }
    @PostMapping("advertisement/watch")
    public ResponseEntity<?> setUpWatchDog(@RequestBody WatchDogDTO watchDogDTO){
        watchDogService.setupWatchDog(watchDogDTO);
        return ResponseEntity.ok("WatchDog set up successfully");
    }

}
