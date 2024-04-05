package com.yellow.foxbuy.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
public class CustomerDTO {
    @NotBlank(message = "Payment Method is required.")
    private String paymentMethod;
    @NotBlank(message = "Full name is required.")
    private String fullName;
    @NotBlank(message = "Address is required.")
    private String address;
}
