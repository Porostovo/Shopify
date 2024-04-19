package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.User;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text) throws MessagingException;

    void sendVerificationEmail(User user) throws MessagingException;

    void sendEmailWithAttachment(String to, String attachmentPath) throws MessagingException;

    void sendMessageToSeller(Authentication authentication, Long id, String message) throws MessagingException;

    void sendEmailWithWatchdogToUser(List<String> userEmails) throws MessagingException;
}
