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

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String path = request.getRequestURI();
        final String method = request.getMethod();

        if ((method.equals("GET") && path.startsWith("/api/vehiculos")) ||
                (method.equals("GET") && path.startsWith("/api/imagenes")) ||
                path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ⚠️ Si no hay token y la ruta es pública, dejamos pasar sin tocar el contexto de seguridad
        if ((authHeader == null || !authHeader.startsWith("Bearer ")) &&
                method.equals("GET") && (path.startsWith("/api/vehiculos") || path.startsWith("/api/imagenes"))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("✅ Token recibido: " + token);

            try {
                String email = jwtUtil.extractUsername(token);
                String rol = jwtUtil.extractRol(token);

                System.out.println("✅ Email extraído del token: " + email);
                System.out.println("✅ Rol extraído del token: " + rol);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (rol != null && !rol.isBlank()) {
                        UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(email)
                                .password("") // sin usar
                                .authorities(rol)
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
        }

        // Siempre continuar con la cadena del filtro
        filterChain.doFilter(request, response);
    }
}







