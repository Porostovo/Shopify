package com.yellow.foxbuy.services.implementations;

import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.interfaces.ConfirmationTokenService;
import com.yellow.foxbuy.services.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    private JavaMailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;

    public EmailServiceImp(ConfirmationTokenService confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override

    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("noreply@baeldung.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        emailSender.send(message);
    }


    @Override
    public void sendVerificationEmail(User user) throws MessagingException {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String email = "Hello, please confirm this link: " +
                "<a href=\"http://localhost:8080/confirm?token=" + confirmationToken.getToken() +
                "\">Click here to confirm</a>";

        sendSimpleMessage(user.getEmail(), "Email Verification", email);
    }

    @Override
    public void sendEmailWithAttachment(String to, String attachmentPath) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String subject = "Your invoice";
        String text = "Hello, congratulation for becoming a VIP user of our FOX BUY application.\n" +
                "Hope you will enjoy all the benefits. Especially unlimited numbers of advertisements.\n" +
                "You will find an invoice attached.\n" +
                "Thank you for using our FOX BUY application.\n" +
                "Sincerely Your Yellow team";

        helper.setFrom("noreply@baeldung.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        // Add Attachment
        attachmentPath = "C:\\Users\\Thinkpad\\Sarka\\FoxBuy\\badius-foxbuy-yellow\\invoice_vip_INV-08-04-2024-001.pdf";
        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

        emailSender.send(message);
    }

}





