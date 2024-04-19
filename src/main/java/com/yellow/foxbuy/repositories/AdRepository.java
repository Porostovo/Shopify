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
    @Query(value = "SELECT * FROM ad WHERE LOWER(title) LIKE %:title%", nativeQuery = true)
    List<Ad> findAllByTitleContainingAnyIgnoreCase(String title);
    @Query(value = "SELECT * FROM ad WHERE LOWER(description) LIKE %:description%", nativeQuery = true)
    List<Ad> findAllByDescriptionContainingAnyIgnoreCase(String description);

//    @Query(value = "SELECT DISTINCT * FROM ad WHERE LOWER(title) LIKE %:search% OR LOWER(description) LIKE %:search%", nativeQuery = true)
//    List<Ad> findAllByTitleOrDescriptionContainingAnyIgnoreCase(@Param("search") String search);

    @Query(value = "SELECT DISTINCT * FROM ad WHERE LOWER(title) LIKE CONCAT('%', LOWER(:search), '%') OR LOWER(description) LIKE CONCAT('%', LOWER(:search), '%')", nativeQuery = true)
    List<Ad> findAllByTitleOrDescriptionContainingAnyIgnoreCase(@Param("search") String search);

//    @Query(value = "SELECT * FROM ad WHERE MATCH(title) AGAINST(:keyword IN NATURAL LANGUAGE MODE)", nativeQuery = true)
//    List<Ad> findAllByTitleOrDescriptionContainingAnyIgnoreCase(@Param("keyword") String keyword);
}

