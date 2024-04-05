package com.yellow.foxbuy.models.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
public class CustomerData {
    public String name;
    public String email;
    public String custromerId;
    private String fullName;
    private String address;


}
