package com.yellow.foxbuy.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;

import com.yellow.foxbuy.models.DTOs.CustomerDTO;

import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.AdManagementServiceImp;
import com.yellow.foxbuy.services.ErrorsHandling;
import com.yellow.foxbuy.services.RoleService;
import com.yellow.foxbuy.services.UserService;
import com.yellow.foxbuy.utils.StripeUtil;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StripePaymentController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final RoleService roleService;
    private final StripeUtil stripeUtil;
    private static final Long vipPrice = 2000L;//20$
    private static final String currency = "usd";


    public StripePaymentController(UserService userService, RoleService roleService, StripeUtil stripeUtil) {
        this.userService = userService;
        this.roleService = roleService;
        this.stripeUtil = stripeUtil;
    }

    @PostMapping("/vip")
    public ResponseEntity<?> processVipPayment(@Valid @RequestBody CustomerDTO customerDTO,
                                               BindingResult bindingResult,
                                               Authentication authentication) throws StripeException {
        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }

        User user = userService.findByUsername(authentication.getName()).orElseThrow();

        if (!AdManagementServiceImp.hasRole(authentication, "ROLE_USER") ||
                !user.getAuthorities().stream().findAny().get().getAuthority().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. You are already  VIP member.");
        }
        //zneplatnit jwttoken?

        final String customerId;

        if (user.getCustomerId() == null) {
            customerId = stripeUtil.createCustomerInStripe(customerDTO, user);
            userService.saveCustomerIdFullNameAndAddress(customerId, customerDTO, user);
        } else {
            customerId = user.getCustomerId();
        }

        PaymentIntent paymentIntent = stripeUtil.createPaymentIntentAndConfirm(vipPrice,
                currency, customerId, customerDTO.getPaymentMethod());

        roleService.setVIPRoleToUser(user);

        if (paymentIntent.getStatus().equals("succeeded")) {
            //SEND INVOICE
            return ResponseEntity.ok("Payment successful. You are now a VIP member!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. Please try again.");
        }
    }
}
