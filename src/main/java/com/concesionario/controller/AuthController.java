package com.concesionario.controller;

import com.concesionario.model.AuthRequest;
import com.concesionario.model.Usuario;
import com.concesionario.repository.UsuarioRepository;
import com.concesionario.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        System.out.println("🔐 Intentando login para: " + request.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);

        if (usuario == null) {
            System.out.println("❌ Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
        System.out.println("🔍 Email recibido: " + request.getEmail());
        System.out.println("🔍 Password recibido: " + request.getPassword());
        System.out.println("🔍 Password hash en la base: " + usuario.getPassword());
        System.out.println("🧪 Coincide?: " + passwordEncoder.matches(request.getPassword(), usuario.getPassword()));

        System.out.println("🔑 Contraseña enviada: " + request.getPassword());
        System.out.println("🔒 Contraseña en BD: " + usuario.getPassword());
        boolean coinciden = passwordEncoder.matches(request.getPassword(), usuario.getPassword());
        System.out.println("✅ ¿Coinciden? " + coinciden);

        if (!coinciden) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "ROLE_" + usuario.getRol());

        String token = jwtUtil.generateToken(claims, usuario.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("rol", usuario.getRol());

        System.out.println("🎫 Token generado correctamente para: " + usuario.getEmail());

        return ResponseEntity.ok(response);
    }
}
