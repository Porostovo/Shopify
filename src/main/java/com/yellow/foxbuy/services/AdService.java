package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdService {
    void saveAd (Ad ad);
    Optional<Ad> findAdById(Long id);
    Ad findAdByIdNoOptional(Long id);
    void deleteAd (Ad ad);

    AdResponseDTO findById(Long id);

    boolean existsById(Long id);

    List<AdResponseDTO> findAllByUser(String username);

    List<AdResponseDTO> findAllByCategoryId(Long id);

    List<AdResponseDTO> listAdsByPageAndCategory(Integer page, Long id);

    int getTotalPages(Long categoryId);
    List<Ad> findAllByUserID(UUID uuid);
    List<Ad> findAllByUsername(String username);
    void updateAd(List<Ad> ad, Boolean cond);
    List<Ad> getHiddenAds(User user);
    boolean isHidden(Ad ad);

}
