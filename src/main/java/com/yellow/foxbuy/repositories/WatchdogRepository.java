package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Watchdog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchdogRepository extends JpaRepository<Watchdog,Long> {
    @Query("SELECT u.email " +
            "FROM Watchdog w " +
            "LEFT JOIN w.user u " +
            "WHERE w.category.id = :categoryId " +
            "AND w.maxPrice >= :price " +
            "AND (w.keyword IS NULL OR :titleDescription LIKE CONCAT('%', w.keyword, '%') OR w.keyword = '')")
    List<String> findMatchingWatchdogs(@Param("categoryId") long categoryId,
                                       @Param("price") double price,
                                       @Param("titleDescription") String titleDescription);
}