package com.concesionario.controller;

import com.concesionario.model.AuthRequest;
import com.concesionario.model.Usuario;
import com.concesionario.repository.UsuarioRepository;
import com.concesionario.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "https://www.automotorescbabuenosaires.com.ar", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        String email = request.getEmail().trim();
        String password = request.getPassword().trim();

        System.out.println("🔐 Intentando login para: " + email);

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            System.out.println("❌ Usuario no encontrado en la base de datos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        System.out.println("🔍 Hash guardado: " + usuario.getPassword());
        System.out.println("🔍 Verificando contraseña...");

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            System.out.println("❌ Contraseña incorrecta");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        // Claims adicionales (como el rol)
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "ROLE_" + usuario.getRol());

        String token = jwtUtil.generateToken(claims, email);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("rol", usuario.getRol());

        System.out.println("✅ Login exitoso, token generado para: " + email);
        System.out.println("📨 Email: " + email);
        System.out.println("🔑 Password enviado: " + password);
        System.out.println("🔒 Hash guardado en DB: " + usuario.getPassword());
        System.out.println("🔄 Coinciden? " + passwordEncoder.matches(password, usuario.getPassword()));

        return ResponseEntity.ok(response);
    }
}

