package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.User;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text) throws MessagingException;

    void sendVerificationEmail(User user) throws MessagingException;
}
