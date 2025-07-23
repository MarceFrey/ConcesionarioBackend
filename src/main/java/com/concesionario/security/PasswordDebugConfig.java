package com.concesionario.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordDebugConfig {

    @Bean
    public CommandLineRunner testPasswordMatch(PasswordEncoder passwordEncoder) {
        return args -> {
            String raw = "admin";
            String hash = "$2a$10$kTME.JLHzBGVnK0VQ53jYOIxxUsLuCr4pMVRozhABd7zQuJk8FCie";
            boolean ok = passwordEncoder.matches(raw, hash);
            System.out.println("🧪 Test interno: ¿'admin' coincide con el hash?: " + ok);
        };
    }
}

