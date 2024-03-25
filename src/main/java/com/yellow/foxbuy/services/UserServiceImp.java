package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void setUserAsVerified(Optional<ConfirmationToken> optionalToken) {
        if (optionalToken.isPresent()) {
            ConfirmationToken confirmationToken = optionalToken.get();
            User user = confirmationToken.getUser();
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
