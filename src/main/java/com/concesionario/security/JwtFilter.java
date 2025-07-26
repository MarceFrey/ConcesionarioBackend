package com.concesionario.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String path = request.getRequestURI();
        final String method = request.getMethod();
        final String authHeader = request.getHeader("Authorization");

        // 🚫 Si es una ruta pública, no procesamos el filtro de JWT
        if ((method.equals("GET") && path.startsWith("/api/vehiculos")) ||
                (method.equals("GET") && path.startsWith("/api/imagenes")) ||
                path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Si hay token, intentamos procesarlo
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("✅ Token recibido: " + token);

            try {
                String email = jwtUtil.extractUsername(token);
                String rol = jwtUtil.extractRol(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (rol != null && !rol.isBlank()) {
                        System.out.println("🔐 Asignando autoridad: " + rol);

                        UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(email)
                                .password("") // no se usa aquí
                                .authorities(new SimpleGrantedAuthority(rol)) // 👈 cambio clave
                                .build();

                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            System.out.println("✅ Usuario autenticado correctamente");
                        } else {
                            System.out.println("❌ Token inválido o expirado.");
                        }
                    } else {
                        System.out.println("❌ Rol inválido en el token.");
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








