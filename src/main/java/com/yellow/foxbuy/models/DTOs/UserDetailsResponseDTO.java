package com.yellow.foxbuy.models.DTOs;

import com.yellow.foxbuy.models.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsResponseDTO {
    private String username;
    private String email;
    private String role;
    private List<AdResponseDTO> ads;
}
