package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearPedidoPresencialRequest {
    private List<DetallePedidoRequest> detalles;
    private String observaciones;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetallePedidoRequest {
        private Long productoId;
        private Integer cantidad;
        private Double precioUnitario;
    }
}