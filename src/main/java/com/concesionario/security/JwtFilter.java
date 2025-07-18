package com.concesionario.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collections;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.replace("Bearer ", "");
            System.out.println("✅ Token recibido: " + token);

            try {
                if (jwtUtil.validarToken(token)) {
                    String email = jwtUtil.extraerEmail(token);
                    String rol = jwtUtil.getRolDesdeToken(token);

                    System.out.println("📧 Email extraído: " + email);
                    System.out.println("🎭 Rol extraído: " + rol);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("🔐 Auth registrada con rol: ROLE_" + rol);
                } else {
                    System.out.println("⚠️ Token inválido (no pasó validación)");
                }
            } catch (Exception e) {
                System.out.println("❌ Error en JwtFilter: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }
        } else {
            System.out.println("⚠️ No se encontró token Authorization válido.");
        }

        filterChain.doFilter(request, response);
    }
}




