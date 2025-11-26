package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockDto {
    private Long id;
    private String nombre;
    private String codigoBarras;
    private Integer stock;
    private Integer stockMin;
}
