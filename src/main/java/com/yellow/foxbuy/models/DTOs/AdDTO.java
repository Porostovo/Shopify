package com.yellow.foxbuy.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AdDTO {
    @NotBlank(message = "Title is required!")
    private String title;
    @NotBlank(message = "Description is required!")
    private String description;
    @NotNull(message ="Price is required!")
    private Double price;
    @NotBlank(message = "Zipcode is required!")
    @Size(max = 5, message = "Zipcode can have maximum of 5 numbers.")
    private String zipcode;
    @NotNull(message = "Category is required!")
    private long categoryID;


}
