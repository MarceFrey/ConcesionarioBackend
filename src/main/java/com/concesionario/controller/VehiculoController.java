package com.concesionario.controller;

import com.concesionario.dto.VehiculoDTO;
import com.concesionario.model.Vehiculo;
import com.concesionario.repository.VehiculoRepository;
import com.concesionario.service.CloudinaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ✅ Listado público
    @GetMapping
    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    // ✅ Detalle público
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtenerPorId(@PathVariable Long id) {
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(id);
        return vehiculo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔐 Crear vehículo con imágenes (requiere ADMIN)
    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Vehiculo> subirVehiculo(
            @ModelAttribute VehiculoDTO datos,
            @RequestParam("imagenes") List<MultipartFile> imagenes
    ) {
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile archivo : imagenes) {
                String url = cloudinaryService.uploadImage(archivo);
                urls.add(url);
            }

            Vehiculo nuevo = new Vehiculo();
            nuevo.setMarca(datos.getMarca());
            nuevo.setModelo(datos.getModelo());
            nuevo.setAnio(datos.getAnio());
            nuevo.setPrecio(datos.getPrecio());
            nuevo.setKilometraje(datos.getKilometraje());
            nuevo.setColor(datos.getColor());
            nuevo.setDescripcion(datos.getDescripcion());
            nuevo.setImagenes(urls);

            Vehiculo guardado = vehiculoRepository.save(nuevo);
            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            System.out.println("❌ Error al subir vehículo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🔐 Editar vehículo (requiere ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizar(@PathVariable Long id, @RequestBody Vehiculo datosActualizados) {
        return vehiculoRepository.findById(id).map(v -> {
            v.setMarca(datosActualizados.getMarca());
            v.setModelo(datosActualizados.getModelo());
            v.setAnio(datosActualizados.getAnio());
            v.setPrecio(datosActualizados.getPrecio());
            v.setKilometraje(datosActualizados.getKilometraje());
            v.setColor(datosActualizados.getColor());
            v.setDescripcion(datosActualizados.getDescripcion());
            v.setImagenes(datosActualizados.getImagenes());
            return ResponseEntity.ok(vehiculoRepository.save(v));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔐 Eliminar vehículo (requiere ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (vehiculoRepository.existsById(id)) {
            vehiculoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


