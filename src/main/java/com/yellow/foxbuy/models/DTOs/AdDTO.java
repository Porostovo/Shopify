package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "Schema used for creating and changing ads")
public class AdDTO {
    @NotBlank(message = "Title is required!")
    @Schema(description = "required", example = "Leviathan Axe")
    private String title;
    @NotBlank(message = "Description is required!")
    @Schema(description = "required", example = "Good axe to kill norse gods. Used, some scratches and blood marks.")
    private String description;
    @NotNull(message ="Price is required!")
    @Schema(description = "required", example = "3000.00")
    private Double price;
    @NotBlank(message = "Zipcode is required!")
    @Size(min = 5, max = 5, message = "Zipcode must be 5 digits long")
    @Schema(description = "required must have maximum 5 characters", example = "12345")
    private String zipcode;
    @NotNull(message = "Category is required!")
    @Schema(description = "required", example = "3")
    private Long categoryID;

    public AdDTO(String title, String description, Double price, String zipcode, Long categoryID) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.categoryID = categoryID;
    }

    @Override
    public String toString() {
        return "title = " + title + " | description = " + description + " | price = " + price + " | zipcode = " + zipcode + " | categoryID = " + categoryID;
    }
}
