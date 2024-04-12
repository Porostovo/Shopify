package com.yellow.foxbuy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledConfig {

    @Scheduled(cron = "0 0 18 * * *") // Run at 6:00 PM every day

    public void deleteInvoices() {
        // Your scheduled task logic goes here
        System.out.println("Executing myScheduledMethod...");
    }
}



