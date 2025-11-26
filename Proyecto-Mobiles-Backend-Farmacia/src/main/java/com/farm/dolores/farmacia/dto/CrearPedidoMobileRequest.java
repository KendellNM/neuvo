package com.farm.dolores.farmacia.dto;

import java.util.List;
import lombok.Data;

@Data
public class CrearPedidoMobileRequest {
    private Long clienteId;
    private Long direccionId;  // Opcional, si selecciona una dirección existente
    private String direccionEntrega;  // Texto de la dirección
    private String telefono;
    private String notas;
    private String metodoPago;
    private Double latitud;
    private Double longitud;
    private List<PedidoDetalleRequest> detalles;
    
    @Data
    public static class PedidoDetalleRequest {
        private Long productoId;
        private Integer cantidad;
        private Double precioUnitario;
    }
}
