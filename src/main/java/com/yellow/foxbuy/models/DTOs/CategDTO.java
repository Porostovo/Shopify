package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CategDTO {
    @Hidden
    private Long id;
    @NotBlank(message = "Category is required.")
    @Schema(description = "required", example = "Beverage")
    private String name;
    @NotBlank(message = "Description is required.")
    @Schema(description = "required", example = "Buy some beer.")
    private String description;

    @Override
    public String toString() {
        return "id = " + id + " | name = " + name + " | description = " + description;
    }
}
