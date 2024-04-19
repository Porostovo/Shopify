package com.yellow.foxbuy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.time.LocalTime;

@Configuration
@EnableScheduling
public class ScheduledConfig {
    private static final String INVOICE_FOLDER_PATH = "resources/generated-PDF";

    @Scheduled(cron = "0 00 18 * * *") //every day at six o clock run this
    public void deleteInvoices() {
        File folder = new File(INVOICE_FOLDER_PATH);
      
        // Get all files in the directory
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".pdf"));

        // Check if any files exist
        if (files == null || files.length == 0) {
            System.out.println("No PDF files found in the directory. " + LocalTime.now());
            return; // Exit method if no PDF files are found
        }

        // Delete all PDF files
        for (File file : files) {
            if (file.delete()) {
                System.out.println("Deleted file: " + file.getName());
            } else {
                System.err.println("Failed to delete file: " + file.getName());
            }
        }
    }
}




