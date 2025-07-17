package com.concesionario.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.*;

@RestController
@RequestMapping("/imagenes")
@CrossOrigin("*")
public class ImagenController {

    private final Path uploadPath = Paths.get("uploads");

    @GetMapping("/{nombreArchivo:.+}")
    public ResponseEntity<Resource> obtenerImagen(@PathVariable String nombreArchivo) {
        try {
            Path archivo = uploadPath.resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(archivo.toUri());

            if (!recurso.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // opcional, podrías hacer detección por extensión
                    .body(recurso);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
