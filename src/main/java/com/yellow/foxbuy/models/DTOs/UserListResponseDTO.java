package com.yellow.foxbuy.models.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserListResponseDTO {
    private String username;
    private String email;
    private String role;
    private Integer ads;
}
