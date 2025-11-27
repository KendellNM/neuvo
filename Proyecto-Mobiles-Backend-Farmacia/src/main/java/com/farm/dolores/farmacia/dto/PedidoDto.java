package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDto {
    private Long id;
    private Integer numeroPedido;
    private Double subtotal;
    private Double descuento;
    private Double costoDelivery;
    private Double total;
    private String metodoPago;
    private String estado;
    private String tipoVenta;
    private Date fechaPedido;
    private Date fechaEntregaEstimada;
    private String observaciones;
    
    // Datos del cliente
    private Long clienteId;
    private String clienteNombre;
    private String clienteTelefono;
    
    // Datos de entrega
    private String direccionEntrega;
    private Double latitud;
    private Double longitud;
    
    // Receta asociada
    private Long recetaDigitalId;
    
    // Productos del pedido
    private List<ProductoPedidoDto> productos;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductoPedidoDto {
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
}
