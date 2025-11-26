package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecetaDigitalDetalleDto {
    private Long id;
    private String medicamentoTexto;
    private Integer cantidad;
    private String dosificacion;
    private Boolean validado;
    private Long productoId;
    private String productoNombre;
}
