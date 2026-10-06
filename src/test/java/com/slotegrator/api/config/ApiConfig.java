package com.slotegrator.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {

    private final Properties properties = new Properties();

    public ApiConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException(
                        "config.properties was not found on the classpath. Copy config.properties.example.");
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load config.properties", exception);
        }
    }

    public String baseUrl() {
        return required("base.url");
    }

    public String basicUsername() {
        return required("basic.username");
    }

    public String basicPassword() {
        return required("basic.password");
    }

    public String accountEmail() {
        return required("account.email");
    }

    public String accountPassword() {
        return required("account.password");
    }

    private String required(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value.trim();
    }
}
