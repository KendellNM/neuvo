package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryLocationDto {
    private Long pedidoId;
    private Long deliveryId;
    private Double latitud;
    private Double longitud;
    private String estado;
    private String timestamp;
}
