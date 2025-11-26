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
public class RecetaDigitalDto {
    private Long id;
    private String imagenUrl;
    private String textoExtraido;
    private String estado;
    private Date fechaCreacion;
    private Date fechaProcesamiento;
    private Long clienteId;
    private String clienteNombre;
    private Long medicoId;
    private String medicoNombre;
    private List<RecetaDigitalDetalleDto> detalles;
}
