package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.RatingDTO;
import com.yellow.foxbuy.models.Rating;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.RatingRepository;
import jakarta.mail.MessagingException;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatingServiceImp implements RatingService{

    private final RatingRepository ratingRepository;
    private final UserService userService;
    private final LogService logService;
    private final EmailService emailService;


    public RatingServiceImp(RatingRepository ratingRepository, UserService userService, LogService logService, EmailService emailService) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
        this.logService = logService;
        this.emailService = emailService;
    }


    @Override
    public ResponseEntity<?> getUserRatings(UUID userId) {
        Map<String, Object> result = new HashMap<>();
        if(!userService.existsById(userId)){
            logService.addLog("GET /user/{id}/rating", "ERROR", "user id: " + userId.toString());
            result.put("error", "User does not exist");
            return ResponseEntity.badRequest().body(result);
        }

        List<Rating> ratingList = ratingRepository.getRatingsByRatedUser(userService.getUserById(userId));
        List<RatingDTO> ratingDTOList = new ArrayList<>();
        if (ratingList.isEmpty()){
            result.put("error", "User has no ratings");
            logService.addLog("GET /user/{id}/rating", "INFO", "user id: " + userId.toString());
            return ResponseEntity.status(200).body(result);
        }
        for (Rating tempRating:ratingList){
            ratingDTOList.add(new RatingDTO(tempRating.getId(), tempRating.getRating(), tempRating.getComment(),
                    (tempRating.getReaction() == null) ? "No response yet" : tempRating.getReaction()));
        }

        result.put("ratings", ratingDTOList);
        logService.addLog("GET /user/{id}/rating", "INFO", "user id: " + userId.toString());
        return ResponseEntity.status(200).body(result);
    }

    @Override
    public ResponseEntity<?> rateUser(UUID toUsernameID, String fromUsername, String message, int rating) throws MessagingException {
        Map<String, Object> result = new HashMap<>();
        User fromUser = userService.getUserByUsernameNotOptional(fromUsername);
        String logMessage = "PathVariable uuid = " + toUsernameID.toString() +
                " | authentication username = " + fromUsername + " | message = " + message + " | rating = " + rating;
        if(!userService.existsById(toUsernameID)){
            logService.addLog("POST /user/rating/{id}", "ERROR", logMessage);
            result.put("error", "User does not exist");
            return ResponseEntity.badRequest().body(result);
        }
        if (toUsernameID.equals(fromUser.getId())){
            logService.addLog("POST /user/rating/{id}", "ERROR", logMessage);
            result.put("error", "User cant comment on his own ad");
            return ResponseEntity.badRequest().body(result);
        }
        if (ratingRepository.existsByRatedUserAndFromUser(userService.getUserById(toUsernameID),
               fromUser.getId())){
            logService.addLog("POST /user/rating/{id}", "ERROR", logMessage);
            result.put("error", "You already sent rating to this user");
            return ResponseEntity.badRequest().body(result);
        }

        Rating rating1 = new Rating(rating, message,
                userService.getUserById(toUsernameID),
                fromUser.getId());
        ratingRepository.save(rating1);
        RatingDTO ratingDTO = new RatingDTO(rating1.getId(), rating1.getRating(), rating1.getComment(), "No response yet");
        emailService.sendRatingNotification(userService.getUserById(toUsernameID));
        logService.addLog("POST /user/rating/{id}", "INFO", logMessage);
        result.put("Your rating was successful:", ratingDTO);
        return ResponseEntity.status(200).body(result);
    }

    @Override
    public ResponseEntity<?> deleteComment(UUID userID, String deleterUsername, Long ratingID) {
        Map<String, Object> result = new HashMap<>();
        String logMessage = "PathVariable uuid = " + userID.toString() +
                " | authentication username = " + deleterUsername +  " | rating id = " + ratingID.toString();

        if(!userService.existsById(userID)){
            logService.addLog("DELETE /user/{id}/rating/{ratingId}/", "ERROR", logMessage);
            result.put("error", "User does not exist");
            return ResponseEntity.badRequest().body(result);
        }
        if (!ratingRepository.existsById(ratingID)){
            logService.addLog("DELETE /user/{id}/rating/{ratingId}/", "ERROR", logMessage);
            result.put("error", "Rating does not exist");
            return ResponseEntity.badRequest().body(result);
        }
        if (userID.equals(userService.getUserByUsernameNotOptional(deleterUsername).getId())
                || userService.getUserByUsernameNotOptional(deleterUsername).getRole().equals("ADMIN")){

            Rating rating = ratingRepository.getReferenceById(ratingID);
            RatingDTO ratingDTO = new RatingDTO(rating.getId(), rating.getRating(), rating.getComment(), rating.getReaction());
            ratingRepository.deleteById(ratingID);
            logService.addLog("DELETE /user/{id}/rating/{ratingId}", "INFO", logMessage);
            result.put("Deletion successful", ratingDTO);
            return ResponseEntity.status(200).body(ratingDTO);
        } else {
            logService.addLog("DELETE /user/{id}/rating/{ratingId}/", "ERROR", logMessage);
            result.put("error", "You cannot delete rating of another user");
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Override
    public ResponseEntity<?> respondToComment(String response, String username, Long ratingID) {
        Map<String, Object> result = new HashMap<>();
        String logMessage = "Response = " + response +
                " | authentication username = " + username +  " | rating id = " + ratingID.toString();

        if (!ratingRepository.existsById(ratingID)) {
            logService.addLog("POST /user/rating/{id}", "ERROR", logMessage);
            result.put("error", "Comment does not exist");
            return ResponseEntity.badRequest().body(result);
        }

        if (ratingRepository.getReferenceById(ratingID).getRatedUser().getId()
                .equals(userService.getUserByUsernameNotOptional(username).getId())){
            Rating rating = ratingRepository.getReferenceById(ratingID);
                   rating.setReaction(response);
            ratingRepository.save(rating);

            result.put("reaction", response);
            logService.addLog("POST /user/rating/{id}", "INFO", logMessage);
            return ResponseEntity.status(200).body(result);
        } else{
            logService.addLog("POST /user/rating/{id}", "ERROR", logMessage);
            result.put("error", "You  cannot respond to rating of another user");
            return ResponseEntity.badRequest().body(result);
        }
    }

}
