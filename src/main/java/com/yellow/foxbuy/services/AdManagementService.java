package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.AdDTO;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AdManagementService {
    ResponseEntity<?> createAd(AdDTO adDTO, Authentication authentication) throws MessagingException;
    ResponseEntity<?> updateAd(Long id, AdDTO adDTO, Authentication authentication) throws MessagingException;
    ResponseEntity<?> deleteAd(Long id, Authentication authentication);
}
