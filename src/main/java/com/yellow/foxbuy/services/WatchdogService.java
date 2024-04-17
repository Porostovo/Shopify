package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.WatchdogDTO;
import com.yellow.foxbuy.models.User;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;

public interface WatchdogService {

    void setupWatchdog(WatchdogDTO watchdogDTO, User user, Authentication authentication);

    void checkWatchdogs(AdDTO adDTO) throws MessagingException;

}
