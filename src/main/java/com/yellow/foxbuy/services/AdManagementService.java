package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.AdDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AdManagementService {
    ResponseEntity<?> createAd(AdDTO adDTO, Authentication authentication);
    ResponseEntity<?> updateAd(Long id, AdDTO adDTO, Authentication authentication);
    ResponseEntity<?> deleteAd(Long id, Authentication authentication);

    boolean isMessageToMyself(Long id, Authentication authentication);
}
