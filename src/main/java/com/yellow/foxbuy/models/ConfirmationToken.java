package com.yellow.foxbuy.models;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import java.time.LocalDateTime;



@Entity
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(nullable = false,name = "user_id")
    private User User;

    public ConfirmationToken(String token,User user) {
        this.token = token;
        this.User = user;
    }

    public ConfirmationToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public com.yellow.foxbuy.models.User getUser() {
        return User;
    }

    public void setUser(com.yellow.foxbuy.models.User user) {
        User = user;
    }
}

