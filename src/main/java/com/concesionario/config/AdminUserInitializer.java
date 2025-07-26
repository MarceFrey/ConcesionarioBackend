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
    public void recrearAdmin() {
        String email = "admin@prueba";
        String passwordPlano = "admin123";  // Esta va a ser tu nueva contraseña segura
        String rol = "ADMIN";

        usuarioRepository.findByEmail(email).ifPresent(usuarioRepository::delete);

        Usuario admin = new Usuario();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(passwordPlano));
        admin.setRol(rol);
        usuarioRepository.save(admin);

        System.out.println("✅ Usuario ADMIN recreado con nueva contraseña: " + passwordPlano);
    }
}

