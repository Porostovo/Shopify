package com.yellow.foxbuy.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
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
}

