package com.yellow.foxbuy.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="User_details")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String token;
    private Boolean verified;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = false;
    }
    public User(String username, String email, String password, Boolean verified ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
    }
}
