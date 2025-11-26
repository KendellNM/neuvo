package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCatalogDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String principioActivo;
    private String imagenUrl;
    private Double precio;
    private Double precioOferta;
    private Boolean requerireReceta;
    private Long categoriaId;
    private String categoriaNombre;
    private Long laboratorioId;
    private String laboratorioNombre;
}
