package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.WatchDog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchDogRepository extends JpaRepository<WatchDog,Long> {
}
