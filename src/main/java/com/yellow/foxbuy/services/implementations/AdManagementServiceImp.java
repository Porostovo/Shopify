package com.yellow.foxbuy.services.implementations;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.interfaces.AdManagementService;
import com.yellow.foxbuy.services.interfaces.AdService;
import com.yellow.foxbuy.services.interfaces.CategoryService;
import com.yellow.foxbuy.services.interfaces.UserService;
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

    @Autowired
    public AdManagementServiceImp(CategoryService categoryService, UserService userService, AdService adService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.adService = adService;
    }

    @Override
    public ResponseEntity<?> createAd(AdDTO adDTO, Authentication authentication) {
        Map<String, String> result = new HashMap<>();
        User user = userService.findByUsername(authentication.getName()).orElse(null);

        boolean isVipUser = hasRole(authentication, "ROLE_VIP");

        if (!isVipUser && user != null && user.getAds().size() >= 3) {
            result.put("error", "User has 3 advertisements. Get VIP user or delete some ad.");
            return ResponseEntity.status(400).body(result);
        }
        // Find the category in repository
        Category category = categoryService.findCategoryById(adDTO.getCategoryID());

        if (category == null) {
            result.put("error", "Category not found.");
            return ResponseEntity.status(400).body(result);
        }
        // Create Ad from AdDTO
        Ad ad = new Ad(adDTO, user, category);

        // Save ad to repository
        try {
            adService.saveAd(ad);
        } catch (Exception e) {
            result.put("error", "Error saving advertisement " + e.getMessage());
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

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<?> updateAd(Long id, AdDTO adDTO, Authentication authentication) {
        Map<String, String> result = new HashMap<>();

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        Optional<Ad> existingAdOptional = adService.findAdById(id);
        Ad existingAd = existingAdOptional.orElse(null);

        if (existingAd == null) {
            result.put("error", "Advertisement not found.");
            return ResponseEntity.status(404).body(result);
        }

        if (!existingAd.getUser().equals(user)) {
            result.put("error", "You are not authorized to update this advertisement");
            return ResponseEntity.status(400).body(result);
        }

        Category category = categoryService.findCategoryById(adDTO.getCategoryID());
        if (category == null) {
            result.put("error", "Category not found.");
            return ResponseEntity.status(400).body(result);
        }

        Ad ad = new Ad(adDTO, user, category);
        ad.setId(id); // Set the ID of the existing advertisement

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
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            result.put("error", "Error saving advertisement: " + e.getMessage());
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
            return ResponseEntity.status(404).body(result);

        }
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");

        if (!isAdmin) {

            if (!existingAd.getUser().equals(user)) {
                result.put("error", "You are not authorized to delete this advertisement");
                return ResponseEntity.status(400).body(result);
            }

        }
        try {
            adService.deleteAd(existingAd);
            result.put("message", "Your ad was deleted");
            return ResponseEntity.status(200).body(result);

        } catch (Exception e) {
            result.put("error", "Error deleting previous advertisement: " + e.getMessage());
            return ResponseEntity.status(400).body(result);

        }

    }

    private boolean hasRole(Authentication authentication, String roleName) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

}
