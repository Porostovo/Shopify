package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.WatchDogDTO;
import com.yellow.foxbuy.models.WatchDog;
import com.yellow.foxbuy.repositories.WatchDogRepository;
import org.springframework.stereotype.Service;

@Service
public class WatchDogServiceImp implements WatchDogService{
    private WatchDogRepository watchDogRepository;
    @Override
    public void setupWatchDog(WatchDogDTO watchDogDTO) {

    }

    @Override
    public WatchDog saveWatchDog(WatchDog watchDog) {
        return watchDogRepository.save(watchDog);
    }
}
