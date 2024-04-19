package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.DTOs.UserDetailsResponseDTO;
import com.yellow.foxbuy.models.DTOs.UserListResponseDTO;
import com.yellow.foxbuy.models.DTOs.CustomerDTO;
import com.yellow.foxbuy.models.User;
import org.springframework.security.core.Authentication;

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
    void saveCustomerIdFullNameAndAddress(String customerId, CustomerDTO customerDTO, User user);
    User getUserById(UUID id);
    User getUserByUsernameNotOptional(String username);
    List<User> getBannedUsers();
    void unbanUser(User user);
    User findByRefreshToken(String refreshToken);
}
