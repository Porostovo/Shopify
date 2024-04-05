package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.DTOs.CustomerDTO;
import com.yellow.foxbuy.models.User;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface UserService {
    User save(User user);
    boolean existsByUsername (String username);
    boolean existsByEmail (String email);

    void setUserAsVerified(Optional<ConfirmationToken> optionalToken);
    Optional<User> findByUsername(String username);

    boolean userRepositoryIsEmpty();


    void saveCustomerIdFullNameAndAddress(String customerId, CustomerDTO customerDTO, User user);
}
