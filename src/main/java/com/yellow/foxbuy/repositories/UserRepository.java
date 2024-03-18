package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername (String username);
    boolean existsByEmail (String email);
}
