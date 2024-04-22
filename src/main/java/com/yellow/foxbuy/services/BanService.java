package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BanService {

    private final UserService userService;
    private final AdService adService;

    @Autowired
    public BanService(UserService userService, AdService adService) {
        this.userService = userService;
        this.adService = adService;
    }

    public Map<String,String> banUser(UUID id, long duration) {
        Map<String,String> result = new HashMap<>();
        User user = userService.getUserById(id);

        if (user == null){
            result.put("error", "User does not exist");
            return result;
        }
        if (duration <1){
            result.put("error", "Wrong ban duration");
            return result;
        }
        user.setBanned(LocalDateTime.now().plusDays(duration));
        userService.save(user);
        adService.updateAd(adService.findAllByUsername(user.getUsername()), true);

            result.put("username", user.getUsername());
            result.put("banned_until", user.getBanned().toString());

        return result;
    }

    @Scheduled (fixedRate = 1000000)
    public void unban(){
        List<User> bannedUsers = userService.getBannedUsers();
        for (User tempUser:bannedUsers){
            if (tempUser.getBanned().isBefore(LocalDateTime.now())){
                adService.updateAd(adService.getHiddenAds(tempUser),false);
                userService.unbanUser(tempUser);
            }
        }
    }
}