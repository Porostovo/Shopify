package com.yellow.foxbuy.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="user_details")
public class  User{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;
    private String password;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ConfirmationToken token;
    private Boolean verified;
    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL)
    private List<Ad> ads = new ArrayList<>();

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
