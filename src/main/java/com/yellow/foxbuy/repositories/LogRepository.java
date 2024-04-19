package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByTimestampBetween (LocalDateTime startOfTheDay, LocalDateTime endOfTheDay);
}
