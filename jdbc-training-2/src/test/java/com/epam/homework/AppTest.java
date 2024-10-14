package com.epam.homework;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class AppTest {
    private App app;

    @BeforeEach
    void setUp() {
        // Create the App instance with a mock properties file path
        String testPropertiesFile = "application-config.properties";
        app = new App(testPropertiesFile);
    }

    @Test
    void testAppRunDoesNotThrowException() {
        // Verify that app.run() does not throw any exceptions
        assertDoesNotThrow(() -> app.run(), "app.run() should not throw an exception");
    }
}

