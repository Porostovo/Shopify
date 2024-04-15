package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
public class CustomerDTO {
    @NotBlank(message = "Payment Method is required.")
    @Schema(description = "required", example = "pm_card_visa")
    private String paymentMethod;
    @NotBlank(message = "Full name is required.")
    @Schema(description = "required", example = "John Tester")
    private String fullName;
    @NotBlank(message = "Address is required.")
    @Schema(description = "required", example = "123 Main Street, Anytown, USA 12345")
    private String address;

    @Override
    public String toString() {
        return "fullName = " + fullName + " | address = " + address + " | paymentMethod = " + paymentMethod;
    }
}
