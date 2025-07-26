package com.concesionario.config;

import com.concesionario.model.Usuario;
import com.concesionario.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void crearAdminSiNoExiste() {
        String email = "admin@prueba";
        String passwordPlano = "admin123";
        String rol = "ADMIN";

        boolean existe = usuarioRepository.findByEmail(email).isPresent();

        usuarioRepository.findByEmail(email).ifPresent(usuarioRepository::delete);

        Usuario admin = new Usuario();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(passwordPlano)); // por ejemplo "admin123"
        admin.setRol(rol);
        usuarioRepository.save(admin);
        System.out.println("✅ Usuario ADMIN recreado con nueva contraseña");

    }
}
