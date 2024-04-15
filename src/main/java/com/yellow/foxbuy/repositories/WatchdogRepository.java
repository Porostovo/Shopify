package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Watchdog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchdogRepository extends JpaRepository<Watchdog,Long> {
}
