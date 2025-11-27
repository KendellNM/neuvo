package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para que el farmacéutico procese una receta y cree el pedido con delivery
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcesarRecetaFarmaceuticoRequest {
    private Long recetaId;
    private List<ProductoPedidoItem> productos;
    private String observacionesFarmaceutico;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductoPedidoItem {
        private Long productoId;
        private Integer cantidad;
    }
}
