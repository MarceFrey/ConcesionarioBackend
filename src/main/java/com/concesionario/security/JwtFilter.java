package com.concesionario.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            System.out.println("✅ Token recibido: " + token);

            try {
                String email = jwtUtil.extractUsername(token);
                String rol = jwtUtil.extractRol(token);

                System.out.println("✅ Email extraído del token: " + email);
                System.out.println("✅ Rol extraído del token: " + rol);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(email)
                            .password("") // no se necesita para validar JWT
                            .authorities(rol) // ← Aquí es donde se inyecta el rol (ej: ROLE_ADMIN)
                            .build();

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("✅ Usuario autenticado correctamente");
                    }
                }

            } catch (Exception e) {
                System.out.println("❌ Error al procesar el token: " + e.getMessage());
            }
        } else {
            System.out.println("❌ No se encontró un token válido en el header Authorization.");
        }

        filterChain.doFilter(request, response);
    }
}






