package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.WatchDogDTO;
import com.yellow.foxbuy.models.WatchDog;

public interface WatchDogService {
    void setupWatchDog(WatchDogDTO watchDogDTO);
    WatchDog saveWatchDog(WatchDog watchdog);

}
