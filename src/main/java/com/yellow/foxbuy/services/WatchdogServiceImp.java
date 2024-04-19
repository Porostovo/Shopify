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
import java.util.Optional;
import java.util.stream.Collectors;

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

    //method to setUp watchdogs
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
    public void findMatchingAdsAndNotifyUsers(AdDTO adDTO, WatchdogDTO watchdogDTO) throws MessagingException {
        Long category_id = adDTO.getCategoryID();
        Double maxPrice = adDTO.getPrice();
        String titleDescription = adDTO.getTitle() + " " + adDTO.getDescription();
        String keyword = watchdogDTO.getKeyword();

        List<Watchdog> matchingWatchdogs = watchdogRepository.findByCategory_IdAndMaxPriceGreaterThan(category_id, maxPrice);

        if (keyword != null && !keyword.isEmpty()) {
            matchingWatchdogs = filterWatchdogsByKeyword(matchingWatchdogs, keyword, titleDescription);
        }

        List<String> userEmails = extractUserEmailsFromWatchdogs(matchingWatchdogs);

        if (!userEmails.isEmpty()) {
            emailService.sendEmailWithWatchdogToUser(userEmails);
        }
    }

    @Override
    public List<Watchdog> filterWatchdogsByKeyword(List<Watchdog> matchingWatchdogs, String keyword, String titleDescription) {
        return matchingWatchdogs.stream()
                .filter(watchdog -> titleDescription.contains(keyword))
                .toList();
    }
    public List<String> extractUserEmailsFromWatchdogs(List<Watchdog> watchdogs) {
        return watchdogs.stream()
                .map(watchdog -> watchdog.getUser() == null ? null : watchdog.getUser().getEmail().trim())
                .filter(email -> email != null && !email.isEmpty())
                .collect(Collectors.toList());
    }


    @Override
    public boolean checkIfWatchdogExists(User user, WatchdogDTO watchdogDTO) {
        // Check if a similar watchdog already exists for the user
        Optional<Watchdog> existingWatchdog;
        if (watchdogDTO.getKeyword() != null && !watchdogDTO.getKeyword().isEmpty()) {
            existingWatchdog = watchdogRepository.findByUserAndCategoryAndMaxPriceAndKeyword(
                    user,
                    categoryService.findCategoryById(watchdogDTO.getCategory_id()),
                    watchdogDTO.getMax_price(),
                    watchdogDTO.getKeyword()
            );
        } else {
            existingWatchdog = watchdogRepository.findByUserAndCategoryAndMaxPrice(
                    user,
                    categoryService.findCategoryById(watchdogDTO.getCategory_id()),
                    watchdogDTO.getMax_price()
            );
        }
        return existingWatchdog.isPresent();
    }
}