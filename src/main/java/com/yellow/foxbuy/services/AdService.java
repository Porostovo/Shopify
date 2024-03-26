package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;

import java.util.Optional;

public interface AdService extends AdManagementService{
    void saveAd (Ad ad);
    Optional<Ad> findAdById(Long id);
    void deleteAd (Ad ad);

}
