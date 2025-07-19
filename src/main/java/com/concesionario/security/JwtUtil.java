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

    private static final String SECRET = System.getenv("SECRET_KEY");

    // Bloque estático para forzar logs apenas se carga la clase
    static {
        System.out.println("🧪 [STATIC] SECRET_KEY: " + SECRET);
        System.out.println("📏 [STATIC] Largo: " + (SECRET != null ? SECRET.length() : "null"));
    }

    private final Key key;

    public JwtUtil() {
        System.out.println("🔑 [Constructor] Clave desde entorno: " + SECRET);
        System.out.println("📏 [Constructor] Longitud de la clave: " + (SECRET != null ? SECRET.length() : "null"));

        if (SECRET == null || SECRET.length() < 32) {
            throw new IllegalStateException("❌ SECRET_KEY no definida o demasiado corta (mínimo 32 caracteres)");
        }

        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRolDesdeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }

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

