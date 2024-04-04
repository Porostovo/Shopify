package com.yellow.foxbuy.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentSource;
import com.stripe.param.*;
import com.yellow.foxbuy.models.DTOs.CustomerData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripePaymentController {
//    @Autowired
//    private PaymentService paymentService;

    @Value("${stripe.apikey}")
    String stripeKey;

    @PostMapping("/createCustomer")
    public ResponseEntity<CustomerData> index(@RequestBody CustomerData data) throws StripeException {
        Stripe.apiKey=stripeKey;
        CustomerCreateParams params = CustomerCreateParams.builder()
                        .setName(data.getName())
                        .setEmail(data.getEmail())
                        .build();
        Customer customer = Customer.create(params);
        data.setCustromerId(customer.getId());

//        Customer customer1 = Customer.retrieve(customer.getId());
//        PaymentSourceCollectionCreateParams params1 =
//                PaymentSourceCollectionCreateParams.builder().setSource("tok_visa").build();
//        PaymentSource paymentSource = customer1.getPaymentSources().create(params1);
//
//        System.out.println(paymentSource.getLastResponse());

        return ResponseEntity.status(200).body(data);
    }

    @PostMapping("/vip")
    public ResponseEntity<String> processVipPayment() throws StripeException {
        Stripe.apiKey=stripeKey;

        PaymentMethodCreateParams paramsC =
                PaymentMethodCreateParams.builder()
                        .setType(PaymentMethodCreateParams.Type.CARD)
                        .setCard(
                                PaymentMethodCreateParams.CardDetails.builder()
                                        .setNumber("4242424242424242")
                                        .setExpMonth(8L)
                                        .setExpYear(2026L)
                                        .setCvc("314")
                                        .build()
                        )
                        .build();
        PaymentMethod paymentMethod = PaymentMethod.create(paramsC);
        System.out.println(paymentMethod.getId());

//        PaymentIntentCreateParams params =PaymentIntentCreateParams.builder()
//                        .setAmount(2000L)
//                        .setCurrency("usd")
//                        //.setPaymentMethod("card")
//                        .setAutomaticPaymentMethods(
//                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//                                        .setEnabled(true)
//                                        .build()
//                        )
//                        .build();
//        PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//        //System.out.println(paymentIntent.);
//        PaymentIntent resource = PaymentIntent.retrieve(paymentIntent.getId());
//
//        PaymentIntentConfirmParams params2 = PaymentIntentConfirmParams.builder()
//                        .setPaymentMethod("pm_card_visa")
//                        .setReturnUrl("https://www.example.com")
//                        .build();
//        PaymentIntent paymentIntent2 = resource.confirm(params2);
//        System.out.println(paymentIntent2.getStatus());


            boolean paymentSuccessful = true; //   paymentService.processPayment(paymentRequest.getName(),
//                paymentRequest.getNumber(),
//                paymentRequest.getValid(),
//                paymentRequest.getCvv());
        if (paymentSuccessful) {
            return ResponseEntity.ok("Payment successful. You are now a VIP member!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. Please try again.");
        }
    }
}
