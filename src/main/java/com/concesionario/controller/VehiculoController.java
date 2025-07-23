package com.concesionario.controller;

import com.concesionario.model.Vehiculo;
import com.concesionario.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoRepository vehiculoRepository;

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

    // 🔐 Crear vehículo (requiere ADMIN)
    @PostMapping("/subir")
    public Vehiculo crear(@RequestBody Vehiculo nuevoVehiculo) {
        return vehiculoRepository.save(nuevoVehiculo);
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

