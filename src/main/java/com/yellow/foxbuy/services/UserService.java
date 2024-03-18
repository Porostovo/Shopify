package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.User;

public interface UserService {
    User save(User user);
    boolean existsByUsername (String username);
    boolean existsByEmail (String email);
}
