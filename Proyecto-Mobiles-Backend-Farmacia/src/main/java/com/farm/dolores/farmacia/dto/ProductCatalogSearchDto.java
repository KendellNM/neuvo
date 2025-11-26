package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCatalogSearchDto {
    private String q; // Buscar libre :)
    private Long categoriaId;
    private Long laboratorioId;
    private Boolean requiereReceta;
    private Integer page = 0;
    private Integer size = 10;
    private String sort = "nombre,asc";
}
