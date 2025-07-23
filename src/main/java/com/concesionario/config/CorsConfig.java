package com.concesionario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://www.automotorescbabuenosaires.com.ar") // Tu frontend
                        .allowedMethods("*") // GET, POST, PUT, DELETE, etc.
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
