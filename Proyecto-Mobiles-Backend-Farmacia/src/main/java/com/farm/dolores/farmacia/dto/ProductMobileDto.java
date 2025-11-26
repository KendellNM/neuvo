package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductMobileDto {
    private Long id;
    private String codigoBarras;
    private String nombre;
    private String descripcion;
    private String principioActivo;
    private String concentracion;
    private Boolean requerireReceta;
    private Double precio;
    private Double precioOferta;
    private Integer stock;
    private String imagenUrl;
    private String indicaciones;
    private String contraindicaciones;
    private String efectosSecundarios;
    private String dosidificador;
    private String categoriaNombre;
    private String laboratorioNombre;
    private Boolean disponible;
}
