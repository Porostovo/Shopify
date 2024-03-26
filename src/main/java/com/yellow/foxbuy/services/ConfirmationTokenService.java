package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken token);
    Optional<ConfirmationToken> getToken(String token);
    String confirmToken(String token);
}
