package com.concesionario.config;

import com.concesionario.model.Usuario;
import com.concesionario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitConfig {

    @Bean
    public CommandLineRunner crearAdminSiNoExiste(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            String emailAdmin = "admin@prueba";

            if (repo.findByEmail(emailAdmin).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setEmail(emailAdmin);
                admin.setPassword(encoder.encode("admin")); // ⚠️ Este será el password real que usás (por ejemplo: "admin")
                admin.setRol("ADMIN");

                repo.save(admin);
                System.out.println("✅ Usuario ADMIN creado correctamente");
            } else {
                System.out.println("ℹ️ Usuario ADMIN ya existe, no se vuelve a crear");
            }
        };
    }
}
