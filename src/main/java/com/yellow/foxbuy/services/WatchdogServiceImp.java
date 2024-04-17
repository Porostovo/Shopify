package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.WatchdogDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.models.Watchdog;
import com.yellow.foxbuy.repositories.WatchdogRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void setupWatchdog(WatchdogDTO watchdogDTO, User user, Authentication authentication) throws RuntimeException {
        Watchdog watchdog = new Watchdog();

        userService.findByUsername(authentication.getName()).orElse(null);

        watchdog.setUser(user);

        Category category = categoryService.findCategoryById(watchdogDTO.getCategory_id());

        watchdog.setCategory(category);

        watchdog.setMaxPrice(watchdogDTO.getMax_price());
        watchdog.setKeyword(watchdogDTO.getKeyword());

        try {
            watchdogRepository.save(watchdog);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the watchdog: " + e.getMessage(), e);
        }
    }

    @Override
    public void checkWatchdogs(AdDTO adDTO) throws MessagingException {
        Long category_id = adDTO.getCategoryID();
        Double maxPrice = adDTO.getPrice();
        String titleDescription = adDTO.getTitle() + " " + adDTO.getDescription();

        List<String> userEmails = watchdogRepository.findMatchingWatchdogs(category_id,maxPrice,titleDescription);

        if (!userEmails.isEmpty()) {
            emailService.sendEmailWithWatchdogToUser(userEmails);
        }
    }

}