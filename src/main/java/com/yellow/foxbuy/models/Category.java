package com.yellow.foxbuy.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank (message = "Category name is required.")
    private String name;
    private String description;
    private Timestamp created_at;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Ad> ads;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.ads = new ArrayList<>();
    }
}
