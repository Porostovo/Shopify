package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Log;
import com.yellow.foxbuy.repositories.LogRepository;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImp implements LogService{

    private final LogRepository logRepository;

    public LogServiceImp(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void addLog(String endpoint, String type, String data) {
        logRepository.save(new Log(endpoint, type, data));
    }
}
