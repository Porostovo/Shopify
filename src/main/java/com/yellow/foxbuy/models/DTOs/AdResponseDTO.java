package com.yellow.foxbuy.models.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AdResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Double price;
    private String zipcode;
    private Long categoryID;

    public AdResponseDTO(Long id, String title, String description, Double price, String zipcode, Long categoryID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.categoryID = categoryID;
    }

}
