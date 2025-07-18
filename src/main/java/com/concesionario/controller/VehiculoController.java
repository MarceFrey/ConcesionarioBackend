package com.concesionario.controller;

import com.concesionario.model.Vehiculo;
import com.concesionario.repository.VehiculoRepository;
import com.concesionario.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin("*")
public class VehiculoController {

    @Autowired
    private VehiculoRepository repo;

    @Autowired
    private com.concesionario.service.CloudinaryService cloudinaryService;

    @PostMapping("/subir")
    public ResponseEntity<Vehiculo> subirVehiculo(
            @RequestParam String marca,
            @RequestParam String modelo,
            @RequestParam int anio,
            @RequestParam double precio,
            @RequestParam int kilometraje,
            @RequestParam String color,
            @RequestParam String descripcion,
            @RequestParam List<MultipartFile> imagenes) throws IOException {

        List<String> urls = new ArrayList<>();
        for (MultipartFile imagen : imagenes) {
            String url = cloudinaryService.uploadImage(imagen);
            urls.add(url); // Guarda la URL pública de Cloudinary
        }

        Vehiculo v = new Vehiculo();
        v.setMarca(marca);
        v.setModelo(modelo);
        v.setAnio(anio);
        v.setPrecio(precio);
        v.setKilometraje(kilometraje);
        v.setColor(color);
        v.setDescripcion(descripcion);
        v.setImagenes(urls);

        return ResponseEntity.ok(repo.save(v));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizarVehiculo(@PathVariable Long id, @RequestBody Vehiculo datos) {
        return repo.findById(id)
                .map(v -> {
                    v.setMarca(datos.getMarca());
                    v.setModelo(datos.getModelo());
                    v.setAnio(datos.getAnio());
                    v.setPrecio(datos.getPrecio());
                    v.setKilometraje(datos.getKilometraje());
                    v.setColor(datos.getColor());
                    v.setDescripcion(datos.getDescripcion());
                    v.setImagenes(datos.getImagenes());
                    return ResponseEntity.ok(repo.save(v));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Vehiculo> listar() {
        return repo.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVehiculo(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
