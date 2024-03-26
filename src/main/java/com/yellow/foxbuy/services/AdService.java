package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;

import java.util.Optional;

public interface AdService {
    Ad saveAd (Ad ad);

    Optional<Ad> findAdById(Long id);

    void deleteAd (Ad ad);
}
