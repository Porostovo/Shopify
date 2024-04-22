package com.yellow.foxbuy.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@Entity
public class Watchdog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private double maxPrice;
    private String keyword;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Watchdog(double maxPrice, String keyword, User user, Category category) {
        this.maxPrice = maxPrice;
        this.keyword = keyword;
        this.user = user;
        this.category = category;
    }
}
