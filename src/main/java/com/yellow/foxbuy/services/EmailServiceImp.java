package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    private JavaMailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;
    private final AdRepository adRepository;
    private final UserRepository userRepository;

    public EmailServiceImp(ConfirmationTokenService confirmationTokenService,
                           AdRepository adRepository,
                           UserRepository userRepository) {
        this.confirmationTokenService = confirmationTokenService;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
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
        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

        emailSender.send(message);
    }

    @Override
    public void sendMessageToSeller(Authentication authentication, Long id, String message) throws MessagingException {
        User user = userRepository.findByUsername(authentication.getName()).get();
        Ad ad = adRepository.findById(id).get();
        String message1 = message + "<br/><br/>" + "You can reply to Sender at: " + user.getEmail();
        sendSimpleMessage(ad.getUser().getEmail(), ad.getTitle(), message1);
    }

    // method to send email to user(s) who has specific watchdog(s)
    @Override
    public void sendEmailWithWatchdogToUser(List<String> userEmails) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String subject = "The ad you were interested in has been created";
        String text = "Hello, recently you set up a watchdog on specific category up to certain price.\n Just before a few moments the ad was created. Look into FOX BUY application";
        helper.setFrom("noreply@baeldung.com");
        String emailString = String.join(", ", userEmails);
        helper.setTo(emailString);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    }
}