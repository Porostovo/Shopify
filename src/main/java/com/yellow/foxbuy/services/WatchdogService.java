package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.WatchdogDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.models.Watchdog;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface WatchdogService {

    void setupWatchdog(WatchdogDTO watchdogDTO, User user, Authentication authentication);

    void checkWatchdogs(AdDTO adDTO, WatchdogDTO watchdogDTO) throws MessagingException;

    List<Watchdog> findMatchingWatchdogs(long category_id, double maxPrice);
}
