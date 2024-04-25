package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
public class RatingDTO {
    private Long id;

    @NotNull(message = "rating cant be empty")
    @Min(1)
    @Max(5)
    @Schema(description = "required, range: 1-5 inc.", example = "5")
    private Integer rating;

    @NotEmpty(message = "comment can´t be empty")
    @Schema(description = "required", example = "You are the best user")
    private String comment;

    private String reaction;

    public RatingDTO(Long id, int rating, String comment, String reaction) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.reaction = reaction;
    }

    public RatingDTO(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "id = " + getId() + " | rating = " + getRating()+ " | comment = " + getComment()
                + " | reaction = " + getReaction();
    }
}
