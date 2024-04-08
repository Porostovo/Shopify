package com.yellow.foxbuy.services.interfaces;

import com.yellow.foxbuy.models.User;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text) throws MessagingException;

    void sendVerificationEmail(User user) throws MessagingException;

    void sendEmailWithAttachment(String to, String attachmentPath) throws MessagingException;
}
