package com.concesionario.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Leer la clave desde variable de entorno
    private static final String SECRET = System.getenv("SECRET_KEY");

    // Clave segura usada para firmar y validar tokens
    private final Key key;

    // Constructor: loguea y valida que la clave sea válida
    public JwtUtil() {
        System.out.println("🔑 Clave desde entorno: " + SECRET);
        System.out.println("📏 Longitud de la clave: " + (SECRET != null ? SECRET.length() : "null"));

        if (SECRET == null || SECRET.length() < 32) {
            throw new IllegalStateException("❌ SECRET_KEY no definida o demasiado corta (mínimo 32 caracteres)");
        }

        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Duración: 24 horas
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // Generar token con email y rol
    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer email (sub)
    public String extraerEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extraer rol
    public String getRolDesdeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }

    // Validar firma y expiración
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("❌ Error al validar token: " + e.getMessage());
            return false;
        }
    }
}
