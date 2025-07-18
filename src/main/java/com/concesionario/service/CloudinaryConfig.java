package com.concesionario.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", System.getenv().getOrDefault("CLOUDINARY_CLOUD_NAME", "tu_cloud_name_local"),
                "api_key", System.getenv().getOrDefault("CLOUDINARY_API_KEY", "tu_api_key_local"),
                "api_secret", System.getenv().getOrDefault("CLOUDINARY_API_SECRET", "tu_api_secret_local")
        );
        return new Cloudinary(config);
    }
}




