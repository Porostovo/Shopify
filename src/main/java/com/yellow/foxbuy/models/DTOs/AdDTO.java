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
    @Size(min = 5, max = 5, message = "Zipcode must be 5 digits long")
    private String zipcode;
    @NotNull(message = "Category is required!")
    private long categoryID;

    public AdDTO(String title, String description, Double price, String zipcode, long categoryID) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.categoryID = categoryID;
    }
}
