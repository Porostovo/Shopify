package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Watchdog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchdogRepository extends JpaRepository<Watchdog,Long> {

    List<Watchdog> findByCategory_IdAndMaxPriceGreaterThan(Long categoryId, Double maxPrice);
}