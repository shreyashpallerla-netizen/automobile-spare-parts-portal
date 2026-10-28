package com.autoparts.integration;

import com.autoparts.AutoPartsApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AutoPartsApplication.class)
@ActiveProfiles("test")
@org.junit.jupiter.api.DisplayName("Application Context Load Test")
class ApplicationContextTest {

    @Test
    @org.junit.jupiter.api.DisplayName("Should load Spring application context successfully")
    void contextLoads() {
        // If this test passes, the Spring context initializes correctly
        assertThat(true).isTrue();
    }
}
