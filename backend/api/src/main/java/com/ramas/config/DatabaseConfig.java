package com.ramas.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:${DATABASE_URL:jdbc:h2:mem:allocflow_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1}}")
    private String dbUrl;

    @Value("${spring.datasource.username:${DATABASE_USERNAME:sa}}")
    private String dbUsername;

    @Value("${spring.datasource.password:${DATABASE_PASSWORD:}}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name:${SPRING_DATASOURCE_DRIVER_CLASS_NAME:}}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        String rawUrl = System.getenv("DATABASE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = dbUrl;
        }

        // Render, Railway, Neon, or standard Cloud PostgreSQL URI handler:
        // Converts: postgres://user:pass@host:port/dbname or postgresql://... -> jdbc:postgresql://host:port/dbname
        if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
            try {
                URI uri = new URI(rawUrl.replace("postgresql://", "postgres://"));
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath();
                String query = uri.getQuery();

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                if (query != null && !query.isBlank()) {
                    jdbcUrl += "?" + query;
                } else if (!jdbcUrl.contains("sslmode=")) {
                    jdbcUrl += "?sslmode=require";
                }

                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    config.setUsername(parts[0]);
                    config.setPassword(parts[1]);
                } else {
                    config.setUsername(dbUsername);
                    config.setPassword(dbPassword);
                }

                config.setJdbcUrl(jdbcUrl);
                config.setDriverClassName("org.postgresql.Driver");
                log.info("Configured cloud PostgreSQL connection: jdbc:postgresql://{}:{}{}", host, port, path);
            } catch (Exception e) {
                log.warn("Failed to parse DATABASE_URL as URI, falling back to direct string: {}", e.getMessage());
                config.setJdbcUrl(rawUrl);
                config.setUsername(dbUsername);
                config.setPassword(dbPassword);
                if (driverClassName != null && !driverClassName.isBlank()) {
                    config.setDriverClassName(driverClassName);
                }
            }
        } else {
            // Local H2 or standard JDBC connection string
            config.setJdbcUrl(rawUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
            if (driverClassName != null && !driverClassName.isBlank()) {
                config.setDriverClassName(driverClassName);
            }
        }

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}
