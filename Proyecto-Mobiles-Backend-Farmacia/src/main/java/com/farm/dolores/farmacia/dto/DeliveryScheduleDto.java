package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryScheduleDto {
    private Long pedidoId;
    private Long repartidorId;
    private Date fechaRecojo;
    private Date fechaEntrega;
    private String observaciones;
}
