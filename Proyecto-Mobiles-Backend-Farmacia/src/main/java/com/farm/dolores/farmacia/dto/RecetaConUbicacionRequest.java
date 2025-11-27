package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para que el cliente envíe una receta con su ubicación de entrega
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecetaConUbicacionRequest {
    private String direccionEntrega;
    private Double latitud;
    private Double longitud;
    private String telefonoContacto;
    private String observaciones;
}
