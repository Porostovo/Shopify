//package com.yellow.foxbuy.services;
//
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Charge;
//import com.stripe.model.Token;
//import com.stripe.param.TokenCreateParams;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class PaymentService {
//    @Value("${stripe.apikey}")
//    String stripeKey;
//
//    public boolean processPayment(String name, String cardNumber, String expirationDate, String cvv) {
//        Stripe.apiKey=stripeKey;
//
//        // Create a map to hold the payment details
//        Map<String, Object> params = new HashMap<>();
//        params.put("amount", 1000); // Example: $10.00, amount in cents
//        params.put("currency", "usd");
//        params.put("description", "Payment for VIP service");
//        params.put("source", createCardToken(cardNumber, expirationDate, cvv)); // Create a card token
//
//        // Attempt to charge the customer
//        try {
//            Charge charge = Charge.create(params);
//            return "succeeded".equals(charge.getStatus()); // Payment successful if charge status is "succeeded"
//        } catch (StripeException e) {
//            e.printStackTrace();
//            return false; // Payment failed
//        }
//    }
//
//    private Map<String, Object> createCardToken(String cardNumber, String expirationDate, String cvv) throws StripeException {
//        TokenCreateParams params = TokenCreateParams.builder()
//                        .setCard(
//                                TokenCreateParams.Card.builder()
//                                        .setNumber(cardNumber)
//                                        .setExpMonth(expirationDate.substring(0, 2))
//                                        .setExpYear(expirationDate.substring(3))
//                                        .setCvc(cvv)
//                                        .build()
//                        )
//                        .build();
//        Token token = Token.create(params);
//
//
//        // Implement logic to create a card token using Stripe API
//        // For security reasons, do not handle card details directly in your code
//        // Instead, use Stripe.js to securely collect card details and obtain a token
//        // Here, we'll simulate creating a token for demonstration purposes
//        Map<String, Object> card = new HashMap<>();
//        card.put("number", cardNumber);
//        card.put("exp_month", expirationDate.substring(0, 2));
//        card.put("exp_year", expirationDate.substring(3));
//        card.put("cvc", cvv);
//        Map<String, Object> tokenParams = new HashMap<>();
//        tokenParams.put("card", card);
//        try {
//            return Token.create(tokenParams);
//        } catch (StripeException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
//
