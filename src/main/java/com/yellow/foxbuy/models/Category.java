package com.yellow.foxbuy.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Schema(description = "Schema used for Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Timestamp created_at;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Ad> ads;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Watchdog> watchdog = new ArrayList<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.ads = new ArrayList<>();
        this.watchdog = new ArrayList<>();
    }
}
