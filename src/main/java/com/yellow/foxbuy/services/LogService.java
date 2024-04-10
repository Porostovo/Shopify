package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Log;

import java.time.LocalDateTime;
import java.util.List;

public interface LogService {
    void addLog(String endpoint, String type, String data);
    List<Log> findAllByDate (LocalDateTime startOfTheDay, LocalDateTime endOfTheDay);
}
