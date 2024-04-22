package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdRepository extends JpaRepository<Ad,Long> {
    List<Ad> findAllByCategoryId(Long id);
    List<Ad> findAllByCategoryIdAndHiddenIsFalse(Long id);
    List<Ad> findAllByUserUsername(String username);
    Page<Ad> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Ad> findByCategoryIdAndHiddenIsFalse(Long categoryId, Pageable pageable);
    long countByCategoryId (Long id);
    List<Ad> findAllByUserId(UUID uuid);
    List<Ad> findAllByUserAndHiddenIsTrue(User user);
    @Query(value = "SELECT DISTINCT * FROM ad WHERE LOWER(title) LIKE %:search% OR LOWER(description) LIKE %:search%", nativeQuery = true)
    List<Ad> findAllByTitleOrDescriptionContainingAnyIgnoreCase(@Param("search") String search);
    Optional<Ad> findByUserAndTitleAndDescriptionAndPriceAndZipcode(User user, String title, String description, Double price, String zipcode);
}

