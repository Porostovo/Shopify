package com.yellow.foxbuy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") //Uses H2 database for testing
class FoxbuyYellowApplicationTests {

    @Test
    void contextLoads() {
    }

}
