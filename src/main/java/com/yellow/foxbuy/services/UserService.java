package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.User;

import java.util.Optional;

public interface UserService {
    User save(User user);
    boolean existsByUsername (String username);
    boolean existsByEmail (String email);

    void setUserAsVerified(Optional<ConfirmationToken> optionalToken);
    Optional<User> findByUsername(String username);

    boolean userRepositoryIsEmpty();
}
