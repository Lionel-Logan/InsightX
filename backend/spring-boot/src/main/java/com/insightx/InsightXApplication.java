package com.insightx;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application Entry Point
 * 
 * This class bootstraps the InsightX backend application.
 * 
 * Responsibilities:
 * - Initialize Spring Boot application context
 * - Enable auto-configuration
 * - Scan for components, services, repositories, and controllers
 * - Start embedded Tomcat server on port 8080
 * - Load environment variables from .env file
 *
 * Configuration:
 * - Uses application.yml for environment-specific settings
 * - Connects to PostgreSQL (localhost:5432)
 * - Connects to Redis (localhost:6379)
 * - Serves REST APIs under /api context path
 *
 * To run: mvn spring-boot:run
 */
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@EnableCaching
@EnableAsync
public class InsightXApplication {

    /**
     * Main entry point for the Spring Boot application.
     * 
     * @param args Command-line arguments passed to the application
     */
    public static void main(String[] args) {
        // Load .env file variables into System properties
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        
        // Set system properties from .env for Spring to use
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
        
        SpringApplication.run(InsightXApplication.class, args);
    }
}