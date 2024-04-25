package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.DTOs.RatingDTO;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface RatingService {

    ResponseEntity<?> getUserRatings(UUID userId);
    ResponseEntity<?> rateUser(UUID ratedUserID, String fromUsername, String message, int rating) throws MessagingException;
    ResponseEntity<?> deleteComment(UUID userID, String deleterUsername ,Long ratingID);
    ResponseEntity<?> respondToComment(String response, String username, Long ratingID);


}
