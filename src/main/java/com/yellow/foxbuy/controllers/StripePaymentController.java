package com.yellow.foxbuy.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.yellow.foxbuy.models.DTOs.CustomerDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.*;
import com.yellow.foxbuy.utils.GeneratePdfUtil;
import com.yellow.foxbuy.utils.StripeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
public class StripePaymentController {

    private final UserService userService;
    private final RoleService roleService;
    private final StripeUtil stripeUtil;
    private final EmailService emailService;
    private final LogService logService;
    private final AuthenticationService authenticationService;
    private static final Long vipPrice = 2000L;//20$
    private static final String currency = "usd";

    @Autowired
    public StripePaymentController(UserService userService,
                                   RoleService roleService,
                                   StripeUtil stripeUtil,
                                   EmailService emailService,
                                   LogService logService,
                                   AuthenticationService authenticationService) {
        this.userService = userService;
        this.roleService = roleService;
        this.stripeUtil = stripeUtil;
        this.emailService = emailService;
        this.logService = logService;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "User pay for VIP account.", description = "User can pay for VIP account. " +
            "Application need to contact 3rd party API (Stripe) with payment details and wait for response, " +
            "if payment was successful or not. If yes, give user VIP Role and send invoice to contact email.")
    @ApiResponse(responseCode = "200", description = "Payment successful. You are now a VIP member!")
    @ApiResponse(responseCode = "400", description = "Payment failed. Please try again.")
    @PostMapping("/vip")
    public ResponseEntity<?> processVipPayment(@Valid @RequestBody CustomerDTO customerDTO,
                                               BindingResult bindingResult,
                                               Authentication authentication)
            throws StripeException, IOException, MessagingException {
        if (bindingResult.hasErrors()) {
            logService.addLog("POST /vip", "ERROR", customerDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        Map<String, String> response = new HashMap<>();

        if (!customerDTO.getPaymentMethod().equals("pm_card_visa")) {
            response.put("error", "This is for testing purposes only, if you want test payments set 'paymentMethod' " +
                    "to 'pm_card_visa' ");
            logService.addLog("POST /vip", "ERROR", customerDTO.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User user = userService.findByUsername(authentication.getName()).orElseThrow();

        if (user.getAuthorities().stream().findAny().get().getAuthority().equals("ROLE_ADMIN")) {
            response.put("error", "Payment failed. You know, as administrator you cannot buy VIP membership.");
            logService.addLog("POST /vip", "ERROR", customerDTO.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!AdManagementServiceImp.hasRole(authentication, "ROLE_USER") ||
                !user.getAuthorities().stream().findAny().get().getAuthority().equals("ROLE_USER")) {
            response.put("error", "Payment failed. You are already  VIP member.");
            logService.addLog("POST /vip", "ERROR", customerDTO.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        final String customerId;

        if (user.getCustomerId() == null) {
            customerId = stripeUtil.createCustomerInStripe(customerDTO, user);
            userService.saveCustomerIdFullNameAndAddress(customerId, customerDTO, user);
        } else {
            customerId = user.getCustomerId();
        }

        PaymentIntent paymentIntent = stripeUtil.createPaymentIntentAndConfirm(vipPrice,
                currency, customerId, customerDTO.getPaymentMethod());

        if (paymentIntent.getStatus().equals("succeeded")) {
            roleService.setVIPRoleToUser(user);

            GeneratePdfUtil generatePdfUtil = new GeneratePdfUtil();
            String invoiceNumber = generatePdfUtil.generateInvoice(user);

            String directoryPath = "resources" + java.io.File.separator + "generated-PDF";
            String fileName = "invoice_vip_" + invoiceNumber + ".pdf";
            String attachmentPath = directoryPath + File.separator + fileName;

            emailService.sendEmailWithAttachment(user.getEmail(), attachmentPath);
            String jwtToken = authenticationService.generateNewJwtToken(user);

            response.put("message", "Payment successful. You are now a VIP member!");
            response.put("jwtToken", jwtToken);
            logService.addLog("POST /vip", "INFO", customerDTO.toString());
            return ResponseEntity.status(200).body(response);
        } else {
            response.put("error", "Payment failed. Please try again.");
            logService.addLog("POST /vip", "ERROR", customerDTO.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
