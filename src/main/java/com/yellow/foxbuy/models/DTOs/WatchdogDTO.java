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
public class WatchdogDTO {
    @NotNull(message = "Category is required!")
    @Schema(description = "required", example = "1")
    private Long category_id;
    @NotNull(message = "Max price is required!")
    @Schema(description = "required", example = "2500")
    private double max_price;
    private String keyword;


    public WatchdogDTO(Long category_id, double max_price, String keyword) {
        this.category_id = category_id;
        this.max_price = max_price;
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "category_id=" + category_id + "| max_price=" + max_price + "| keyword='" + keyword;
    }
}
