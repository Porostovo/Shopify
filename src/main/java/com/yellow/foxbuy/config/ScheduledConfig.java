package com.yellow.foxbuy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.util.Calendar;

@Configuration
@EnableScheduling
public class ScheduledConfig {
    private static final String INVOICE_FOLDER_PATH = "/resources/generated-PDF";



    @Scheduled(cron = "0 0 18 * * *") //every day at six o clock run this

    public void deleteInvoices() {
        File folder = new File(INVOICE_FOLDER_PATH);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                Calendar today = Calendar.getInstance();
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".pdf")) {
                        Calendar fileModified = Calendar.getInstance();
                        fileModified.setTimeInMillis(file.lastModified());
                        if (isSameDay(today, fileModified)) {
                            boolean deleted = file.delete();
                            if (deleted) {
                                System.out.println("Deleted file: " + file.getName());
                            } else {
                                System.err.println("Failed to delete file: " + file.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}




