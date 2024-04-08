package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
