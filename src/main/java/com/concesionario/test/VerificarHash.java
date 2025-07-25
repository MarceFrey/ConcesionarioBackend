package com.concesionario.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerificarHash {
    public static void main(String[] args) {
        String passwordPlano = "admin123";
        String hashGuardado = "$2a$10$IeM6XgwUoecNGWevDDZBkuAz9e3AIY1AbzpJGDagp1UV0ITiNlP1a";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean coincide = encoder.matches(passwordPlano, hashGuardado);

        System.out.println("¿Coinciden?: " + coincide);
    }
}
