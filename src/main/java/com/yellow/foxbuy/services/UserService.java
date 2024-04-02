package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.DTOs.UserDetailsResponseDTO;
import com.yellow.foxbuy.models.DTOs.UserListResponseDTO;
import com.yellow.foxbuy.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User save(User user);
    boolean existsByUsername (String username);
    boolean existsByEmail (String email);

    void setUserAsVerified(Optional<ConfirmationToken> optionalToken);
    Optional<User> findByUsername(String username);

    boolean userRepositoryIsEmpty();

    UserDetailsResponseDTO getDetailsById (UUID id) throws Exception;
    boolean existsById (UUID id);
    List<UserListResponseDTO> listUsersByPage(Integer page);
    int getTotalPages (List<User> users);
    List<User> getAllUsers();
}
