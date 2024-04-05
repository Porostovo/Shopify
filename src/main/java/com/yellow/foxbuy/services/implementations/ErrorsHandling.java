package com.yellow.foxbuy.services.implementations;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ErrorsHandling {
    public static ResponseEntity<?> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> result = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            result.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(400).body(result);
    }
}
