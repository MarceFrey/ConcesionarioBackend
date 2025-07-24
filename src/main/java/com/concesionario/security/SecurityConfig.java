package com.concesionario.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ✅ Login y registro públicos
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()


                        // ✅ GET públicos para ver vehículos e imágenes
                        .requestMatchers(HttpMethod.GET, "/api/vehiculos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vehiculos/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/imagenes/**").permitAll()

                        // 🔐 Protegido: subir, editar, borrar vehículos (solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/vehiculos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/vehiculos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/vehiculos/**").hasRole("ADMIN")

                        // 🔐 Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}






