package com.yellow.foxbuy.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Double price;
    private LocalDateTime localDateTime;
    private String zipcode;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Ad(String title, String description, Double price, String zipcode){
        this.title = title;
        this.description = description;
        this.price = price;
        this.localDateTime  = LocalDateTime.now();
        this.zipcode = zipcode;
    }
}
