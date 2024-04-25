package com.yellow.foxbuy.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int rating;

    private String comment;
    private String reaction;
    @ManyToOne
    private User ratedUser;


    private UUID fromUser;

    public Rating(int rating, String comment, User ratedUser, UUID fromUser) {
        this.rating = rating;
        this.comment = comment;
        this.ratedUser = ratedUser;
        this.fromUser = fromUser;

    }
}
