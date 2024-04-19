package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad,Long> {
    List<Ad> findAllByCategoryId(Long id);
    List<Ad> findAllByUserUsername(String username);
    Page<Ad> findByCategoryId(Long categoryId, Pageable pageable);
    long countByCategoryId (Long id);
    Optional<Ad> findByUserAndTitleAndDescriptionAndPriceAndZipcode(User user, String title, String description, Double price, String zipcode);
}

