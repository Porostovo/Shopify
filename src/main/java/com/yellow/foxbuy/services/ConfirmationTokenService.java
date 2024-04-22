package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.ConfirmationToken;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken token);

    String confirmToken(String token);
}
