package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.WatchdogDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.models.Watchdog;
import org.springframework.security.core.Authentication;

public interface WatchdogService {

    void setupWatchdog(WatchdogDTO watchdogDTO, User user, Authentication authentication);

    Watchdog saveWatchdog(Watchdog watchdog);

}
