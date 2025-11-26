package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryStatusUpdateDto {
    private Long pedidoId;
    private String estado;
    private String mensaje;
    private Double latitud;
    private Double longitud;
    private String deliveryNombre;
    private String timestamp;
}
