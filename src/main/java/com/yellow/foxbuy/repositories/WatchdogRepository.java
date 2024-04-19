package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.models.Watchdog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchdogRepository extends JpaRepository<Watchdog,Long> {

    List<Watchdog> findByCategory_IdAndMaxPriceGreaterThan(Long categoryId, Double maxPrice);

    Optional<Watchdog> findByUserAndCategoryAndMaxPrice(User user, Category categoryById, double maxPrice);

    Optional<Watchdog> findByUserAndCategoryAndMaxPriceAndKeyword(User user, Category categoryById, double maxPrice, String keyword);
}