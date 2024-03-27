package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad,Long> {
    List<Ad> findAllByCategoryId(Long id);
    List<Ad> findAllByUserUsername(String username);
}
