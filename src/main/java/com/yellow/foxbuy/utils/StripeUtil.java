package com.yellow.foxbuy.utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.yellow.foxbuy.models.DTOs.CustomerDTO;
import com.yellow.foxbuy.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeUtil {
    @Value("${stripe.apikey}")
    private String stripeKey;

    public String createCustomerInStripe(CustomerDTO customerDTO, User user) throws StripeException {
        Stripe.apiKey = stripeKey;

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(customerDTO.getFullName())
                .setEmail(user.getEmail())
                .build();
        Customer customer = Customer.create(params);
        return customer.getId();
    }

    public PaymentIntent createPaymentIntentAndConfirm(Long amount,
                                                       String currency,
                                                       String customerId,
                                                       String paymentMethod) throws StripeException {

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setCustomer(customerId)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        PaymentIntentConfirmParams params2 = PaymentIntentConfirmParams.builder()
                .setPaymentMethod(paymentMethod)//test payment method is "pm_card_visa"
                .setReturnUrl("https://www.example.com")
                .build();
        return paymentIntent.confirm(params2);
    }
}
