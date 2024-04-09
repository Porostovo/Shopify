package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Log;

public interface LogService {
    void addLog(String endpoint, String type, String data);
}
