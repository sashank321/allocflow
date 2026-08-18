package com.ramas.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    // Default 256-bit secret key for development/demo (override with env var JWT_SECRET in prod)
    private String secret = "v7Q4sE9yW2zA8uK1pM5rT0hN3bV6cX8dF2gJ4lK9qZ1xV3mB7eP0sD4fG6hJ8kL2";
    private long expirationMs = 86400000L; // 24 hours

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}
