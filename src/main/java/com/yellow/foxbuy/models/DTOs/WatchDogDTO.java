package com.yellow.foxbuy.models.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "Schema used for watchdog")
public class WatchDogDTO {
    @NotNull(message = "Category is required!")
    @Schema(description = "required", example = "1")
    private Long category_id;
    @NotNull(message = "Max price is required!")
    @Schema(description = "required", example = "2500")
    private Long max_price;
    private Long keyword;

}
