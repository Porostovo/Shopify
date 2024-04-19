package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BanDTO {

    @Schema(description = "required", example = "5")
    @NotNull(message = "Duration is required.")
    private Integer duration;

    public BanDTO(Integer duration) {
        this.duration = duration;
    }
}
