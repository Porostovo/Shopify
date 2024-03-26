package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface AdService {
    Ad saveAd (Ad ad);

    Optional<Ad> findAdById(Long id);

    void deleteAd (Ad ad);
    boolean isUserTheOwnerOfAd(User user, Long adId);
    ResponseEntity<?> createAd(AdDTO adDTO, Authentication authentication);
    ResponseEntity<?> updateAd(Long id, AdDTO adDTO, Authentication authentication);
    ResponseEntity<?> deleteAd(Long id, Authentication authentication);

}
