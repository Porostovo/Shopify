package com.yellow.foxbuy.models.DTOs;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class AuthResponseDTO {
    private String message;
    private String token;
}
