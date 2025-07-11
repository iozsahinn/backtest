package com.optimizer;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply CORS to all paths under /api/
                .allowedOrigins(
                        "http://localhost:4200",        // for local dev
                        "http://91.99.183.111",         // for server without port
                        "http://91.99.183.111:4200"     // if frontend runs on this port
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("*") // Allowed request headers
                .allowCredentials(true) // Allow cookies, authorization headers, etc.
                .maxAge(3600); // Cache preflight response for 1 hour
    }
}