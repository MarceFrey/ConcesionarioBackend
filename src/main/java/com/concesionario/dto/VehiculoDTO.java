// VehiculoDTO.java
package com.concesionario.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculoDTO {
    private String marca;
    private String modelo;
    private int anio;
    private double precio;
    private int kilometraje;
    private String color;
    private String descripcion;
}
