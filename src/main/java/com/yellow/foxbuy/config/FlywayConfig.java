//package com.yellow.foxbuy.config;
//
//import org.flywaydb.core.Flyway;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FlywayConfig {
//
//    @Bean
//    public Flyway flyway() {
//        String username = System.getenv("DB_USERNAME");
//        String password = System.getenv("DB_PASSWORD");
//
//        Flyway flyway = Flyway.configure()
//                .dataSource("jdbc:mysql://localhost:3306/foxbuy", username, password)
////                .locations("classpath:db/migration")
//                .load();
//
//        flyway.baseline();
//        flyway.migrate();
//        return flyway;
//    }
//}
