package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.models.DTOs.UserDetailsResponseDTO;
import com.yellow.foxbuy.models.DTOs.UserListResponseDTO;
import com.yellow.foxbuy.models.DTOs.CustomerDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final AdRepository adRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository, AdRepository adRepository) {
        this.userRepository = userRepository;
        this.adRepository = adRepository;
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

    @Override
    public boolean userRepositoryIsEmpty() {
        return !userRepository.existsBy();
    }

    @Override
    public UserDetailsResponseDTO getDetailsById(UUID id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("User not found."));
        UserDetailsResponseDTO userResponse = new UserDetailsResponseDTO();
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        List<AdResponseDTO> adResponseDTOList = new ArrayList<>();

        List<Ad> allAdsOfUser = adRepository.findAllByUserUsername(user.getUsername());
        for (Ad ad : allAdsOfUser){
            AdResponseDTO adResponseDTO = new AdResponseDTO();
            adResponseDTO.setId(ad.getId());
            adResponseDTO.setTitle(ad.getTitle());
            adResponseDTO.setDescription(ad.getDescription());
            adResponseDTO.setPrice(ad.getPrice());
            adResponseDTO.setZipcode(ad.getZipcode());
            adResponseDTO.setCategoryID(ad.getCategory().getId());
            adResponseDTOList.add(adResponseDTO);
        }

        userResponse.setAds(adResponseDTOList);
        return userResponse;
    }

    @Override
    public boolean existsById(UUID id) {
            return userRepository.existsById(id);
    }

    @Override
    public List<UserListResponseDTO> listUsersByPage(Integer page) {
        int pageSize = 10;
        List<UserListResponseDTO> userListDTO = new ArrayList<>();

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<User> userList = userRepository.findAll(pageable).getContent();
        for (User user : userList) {
            UserListResponseDTO userDetailDTO = new UserListResponseDTO();
            userDetailDTO.setUsername(user.getUsername());
            userDetailDTO.setEmail(user.getEmail());
            userDetailDTO.setRole(user.getRole());
            userDetailDTO.setAds(user.getAds().size());
            userListDTO.add(userDetailDTO);
        }
        return userListDTO;
    }

    @Override
    public int getTotalPages(List<User> users) {
        return (int) Math.ceil((double) users.size() / 10.0);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveCustomerIdFullNameAndAddress(String customerId, CustomerDTO customerDTO, User user) {
        user.setAddress(customerDTO.getAddress());
        user.setCustomerId(customerId);
        user.setFullName(customerDTO.getFullName());
        userRepository.save(user);
    }
    @Override
    public User getUserById(UUID id) {
        Optional<User> optUser =  userRepository.findUserById(id);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        else {
            return null;
        }
    }

    @Override
    public User getUserByUsernameNotOptional(String username) {
        Optional<User> optUser =  findByUsername(username);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        else {
            return null;
        }
    }

    @Override
    public List<User> getBannedUsers() {
        return userRepository.findAllByBannedIsNotNull();
    }

    @Override
    public void unbanUser(User user) {
        user.setBanned(null);
        userRepository.save(user);
    }

    @Override
    public User findByRefreshToken(String refreshToken) {
        return userRepository.findFirstByRefreshToken(refreshToken);
    }
}
