package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsername (String username);
    boolean existsByEmail (String email);
    Optional<User> findByUsername(String username);
    boolean existsBy();
    Page<User> findAll (Pageable pageable);
    Optional<User> findUserById(UUID id);
    List<User> findAllByBannedIsNotNull();
    boolean existsById (UUID id);
    User findFirstByRefreshToken(String refreshToken);
}
