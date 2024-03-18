package com.yellow.foxbuy.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImp implements EmailService{

    public void sendSimpleMessage(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@GFAisinInsolvency.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);


    }



}
