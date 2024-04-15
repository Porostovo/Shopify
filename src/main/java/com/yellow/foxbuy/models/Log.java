package com.yellow.foxbuy.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String endpoint;
    private String type;
    private LocalDateTime timestamp;
    private String data;

    public Log(String endpoint, String type, String data) {
        this.endpoint = endpoint;
        this.type = type;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
