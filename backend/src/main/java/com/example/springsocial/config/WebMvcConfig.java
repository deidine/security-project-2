package com.example.springsocial.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOrigins("http://localhost:4200") // Add your frontend URL here
        .allowedMethods("GET", "POST", "PUT", "DELETE") // Add allowed HTTP methods
        .allowedHeaders("*") // Add allowed headers
        .allowCredentials(true).maxAge(MAX_AGE_SECS); 

        // registry.addMapping("/**")
        // .allowedOrigins("*")
        // .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        // .allowedHeaders("*")
        // .allowCredentials(true)
        // .maxAge(MAX_AGE_SECS);
    }
}
