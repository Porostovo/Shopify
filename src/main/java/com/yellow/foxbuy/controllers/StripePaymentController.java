package com.yellow.foxbuy.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.yellow.foxbuy.models.DTOs.CustomerDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.AdManagementServiceImp;
import com.yellow.foxbuy.services.ErrorsHandling;
import com.yellow.foxbuy.services.RoleService;
import com.yellow.foxbuy.services.UserService;
import com.yellow.foxbuy.utils.StripeUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class StripePaymentController {

    private final UserService userService;
    private final RoleService roleService;
    private final StripeUtil stripeUtil;
    private static final Long vipPrice = 2000L;//20$
    private static final String currency = "usd";

    @Autowired
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
        Map<String, String> response = new HashMap<>();

        if (!customerDTO.getPaymentMethod().equals("pm_card_visa")) {
            response.put("error", "This is for testing purposes only, if you want test payments set 'paymentMethod' to 'pm_card_visa' ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User user = userService.findByUsername(authentication.getName()).orElseThrow();

        if (!AdManagementServiceImp.hasRole(authentication, "ROLE_USER") ||
                !user.getAuthorities().stream().findAny().get().getAuthority().equals("ROLE_USER")) {
            response.put("error", "Payment failed. You are already  VIP member.");
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

            //todo SEND INVOICE (customerDTO.getFullName(), customerDTO.getAddress())

            response.put("message", "Payment successful. You are now a VIP member!");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Payment failed. Please try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
