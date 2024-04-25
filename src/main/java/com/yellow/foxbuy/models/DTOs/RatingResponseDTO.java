package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RatingResponseDTO {
    @NotBlank(message = "Reaction cannot be empty")
    @Schema(description = "required", example = "Thank you for your comment")
    private String reaction;

    public RatingResponseDTO(String reaction) {
        this.reaction = reaction;
    }
}
