package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;

import java.util.List;
import java.util.Optional;

public interface AdService {
    Ad saveAd (Ad ad);

    Optional<Ad> findAdById(Long id);

    void deleteAd (Ad ad);

    AdResponseDTO findById(Long id);

    boolean existsById(Long id);
}
