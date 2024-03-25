package com.yellow.foxbuy.models.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AdResponseDTO {
    //look into JIRA, what is id (10) -example
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String zipcode;
    private long categoryID;
}
