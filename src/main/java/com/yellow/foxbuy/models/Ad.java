package com.yellow.foxbuy.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yellow.foxbuy.models.DTOs.AdDTO;
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
    @JsonIgnore
    private LocalDateTime localDateTime;
    private String zipcode;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private boolean hidden;

    public Ad(AdDTO adDTO, User user, Category category) {
        this.title = adDTO.getTitle();
        this.description = adDTO.getDescription();
        this.price = adDTO.getPrice();
        this.zipcode = adDTO.getZipcode();
        this.user = user;
        this.localDateTime = LocalDateTime.now();
        this.category = category;
        this.hidden = false;
    }
    public Ad(String title, String description, double price, String zipcode, User user, Category category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.user = user;
        this.category = category;
        this.localDateTime = LocalDateTime.now();
        this.hidden = false;
    }

    public Ad(String title, Category category) {
        this.title = title;
        this.category = category;
        this.hidden = false;
    }

    public Ad(String title, String description, double price, String zipcode, Category category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.category = category;
        this.hidden = false;
    }
}
