package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.models.DTOs.WatchdogDTO;
import com.yellow.foxbuy.models.User;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AdManagementServiceImp implements AdManagementService {
    private final CategoryService categoryService;
    private final UserService userService;
    private final AdService adService;
    private final LogService logService;
    private final WatchdogService watchdogService;

    @Autowired
    public AdManagementServiceImp(CategoryService categoryService, UserService userService, AdService adService, LogService logService, WatchdogService watchdogService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.adService = adService;
        this.logService = logService;
        this.watchdogService = watchdogService;
    }

    @Override
    public ResponseEntity<?> createAd(AdDTO adDTO, Authentication authentication, WatchdogDTO watchdogDTO) throws MessagingException {
        Map<String, String> result = new HashMap<>();
        User user = userService.findByUsername(authentication.getName()).orElse(null);

        boolean isVipUser = hasRole(authentication, "ROLE_VIP");

        if (!isVipUser && user != null && user.getAds().size() >= 3) {
            result.put("error", "User has 3 advertisements. Get VIP user or delete some ad.");
            logService.addLog("POST /advertisement", "ERROR", adDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
        // Check if the ad already exists for the user
        boolean adExists = adService.checkIfAdExists(user, adDTO);
        if (adExists) {
            result.put("error", "This advertisement already exists for this user.");
            logService.addLog("POST /advertisement", "ERROR", adDTO.toString());
            return ResponseEntity.status(400).body(result);
        }

        // Find the category in repository
        Category category = categoryService.findCategoryById(adDTO.getCategoryID());

        if (category == null) {
            result.put("error", "Category not found.");
            logService.addLog("POST /advertisement", "ERROR", adDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
        // Create Ad from AdDTO
        Ad ad = new Ad(adDTO, user, category);
        // Check if there is watchdog for this ad
        watchdogService.findMatchingAdsAndNotifyUsers(adDTO, watchdogDTO);

        // Save ad to repository
        try {
            adService.saveAd(ad);
        } catch (Exception e) {
            result.put("error", "Error saving advertisement " + e.getMessage());
            logService.addLog("POST /advertisement", "ERROR", adDTO.toString());
            return ResponseEntity.status(400).body(result);
        }

        // Return response with ad id
        AdResponseDTO response = new AdResponseDTO(
                ad.getId(),
                adDTO.getTitle(),
                adDTO.getDescription(),
                adDTO.getPrice(),
                adDTO.getZipcode(),
                adDTO.getCategoryID()
        );
        logService.addLog("POST /advertisement", "INFO", adDTO.toString());
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<?> updateAd(Long id, AdDTO adDTO, Authentication authentication, WatchdogDTO watchdogDTO) throws MessagingException {
        Map<String, String> result = new HashMap<>();

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        Optional<Ad> existingAdOptional = adService.findAdById(id);
        Ad existingAd = existingAdOptional.orElse(null);

        if (existingAd == null) {
            result.put("error", "Advertisement not found.");
            logService.addLog("PUT /advertisement/{id}", "ERROR", "id = " + id + " | " + adDTO.toString());
            return ResponseEntity.status(404).body(result);
        }

        if (!existingAd.getUser().equals(user)) {
            result.put("error", "You are not authorized to update this advertisement");
            logService.addLog("PUT /advertisement/{id}", "ERROR", "id = " + id + " | " + adDTO.toString());
            return ResponseEntity.status(400).body(result);
        }

        Category category = categoryService.findCategoryById(adDTO.getCategoryID());
        if (category == null) {
            result.put("error", "Category not found.");
            logService.addLog("PUT /advertisement/{id}", "ERROR", "id = " + id + " | " + adDTO.toString());
            return ResponseEntity.status(400).body(result);
        }

        Ad ad = new Ad(adDTO, user, category);
        ad.setId(id); // Set the ID of the existing advertisement

        // Check if there is watchdog for this ad
        watchdogService.findMatchingAdsAndNotifyUsers(adDTO, watchdogDTO);

        try {
            adService.saveAd(ad);
            // Return response with ad id
            AdResponseDTO response = new AdResponseDTO(
                    id,
                    adDTO.getTitle(),
                    adDTO.getDescription(),
                    adDTO.getPrice(),
                    adDTO.getZipcode(),
                    adDTO.getCategoryID()
            );

            logService.addLog("PUT /advertisement/{id}", "INFO", "id = " + id + " | " + adDTO.toString());
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            result.put("error", "Error saving advertisement: " + e.getMessage());
            logService.addLog("PUT /advertisement/{id}", "ERROR", "id = " + id + " | " + adDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
    }

    @Override
    public ResponseEntity<?> deleteAd(Long id, Authentication authentication) {
        Map<String, String> result = new HashMap<>();

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        Optional<Ad> existingAdOptional = adService.findAdById(id);
        Ad existingAd = existingAdOptional.orElse(null);

        if (existingAd == null) {
            result.put("error", "Advertisement not found");
            logService.addLog("DELETE /advertisement/{id}", "ERROR", "id = " + id);
            return ResponseEntity.status(404).body(result);

        }
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");

        if (!isAdmin) {

            if (!existingAd.getUser().equals(user)) {
                result.put("error", "You are not authorized to delete this advertisement");
                logService.addLog("DELETE /advertisement/{id}", "ERROR", "id = " + id);
                return ResponseEntity.status(400).body(result);
            }

        }
        try {
            adService.deleteAd(existingAd);
            result.put("message", "Your ad was deleted");
            logService.addLog("DELETE /advertisement/{id}", "INFO", "id = " + id);
            return ResponseEntity.status(200).body(result);

        } catch (Exception e) {
            result.put("error", "Error deleting previous advertisement: " + e.getMessage());
            logService.addLog("DELETE /advertisement/{id}", "ERROR", "id = " + id);
            return ResponseEntity.status(400).body(result);

        }

    }

    @Override
    public boolean isMessageToMyself(Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        Optional<Ad> existingAdOptional = adService.findAdById(id);
        Ad existingAd = existingAdOptional.orElseThrow();

        return user.getId() == existingAd.getUser().getId();
    }

    public static boolean hasRole(Authentication authentication, String roleName) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

}
