package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;

import java.util.List;
import java.util.Optional;

public interface AdService {
    void saveAd (Ad ad);
    Optional<Ad> findAdById(Long id);
    void deleteAd (Ad ad);

    AdResponseDTO findById(Long id);

    boolean existsById(Long id);

    List<AdResponseDTO> findAllByUser(String username);

    List<AdResponseDTO> findAllByCategoryId(Long id);

    List<AdResponseDTO> listAdsByPageAndCategory(Integer page, Long id);

    int getTotalPages(Long categoryId);

}
