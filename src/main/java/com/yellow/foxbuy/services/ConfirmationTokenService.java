package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.repositories.ConfirmationTokenRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.Optional;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserService userService;

@Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository, UserService userService) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    this.userService = userService;
}
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional <ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public String confirmToken(String token) {
        Optional<ConfirmationToken> optionalToken = confirmationTokenRepository.findByToken(token);

        if (optionalToken.isPresent()) {
            userService.setUserAsVerified(optionalToken);
            return "Confirmed";
        } else {
            return "Token not found.";
        }
    }
}
