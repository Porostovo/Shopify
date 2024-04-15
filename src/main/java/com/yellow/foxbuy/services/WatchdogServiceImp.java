package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.WatchdogDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.models.Watchdog;
import com.yellow.foxbuy.repositories.WatchdogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.yellow.foxbuy.services.AdManagementServiceImp.hasRole;

@Service
public class WatchdogServiceImp implements WatchdogService {

    private final WatchdogRepository watchdogRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final CategoryService categoryService;
    @Autowired
    public WatchdogServiceImp(WatchdogRepository watchdogRepository, EmailService emailService, UserService userService, CategoryService categoryService) {
        this.watchdogRepository = watchdogRepository;

        this.emailService = emailService;
        this.userService = userService;
        this.categoryService = categoryService;
    }
    @Override
    public void setupWatchdog(WatchdogDTO watchdogDTO, User user, Authentication authentication) {
        Watchdog watchdog = new Watchdog();

       userService.findByUsername(authentication.getName()).orElse(null);

        boolean isVipUser = hasRole(authentication, "ROLE_VIP");

        // Set the user for the Watchdog
        watchdog.setUser(user);

        // Retrieve the Category entity based on the provided category ID
        Category category = categoryService.findCategoryById(watchdogDTO.getCategory_id());

        // Set the category for the Watchdog
        watchdog.setCategory(category);

        // Set other properties like max price and keyword
        watchdog.setMaxPrice(watchdogDTO.getMax_price());
        watchdog.setKeyword(watchdogDTO.getKeyword());

        watchdogRepository.save(watchdog);
    }


    @Override
    public Watchdog saveWatchdog(Watchdog watchdog) {
        return watchdogRepository.save(watchdog);
    }
}
