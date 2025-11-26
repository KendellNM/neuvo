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
public class ProgramaFidelizacionDto {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Integer puntosActuales;
    private Integer puntosAcumulados;
    private String nivelMembresia;
    private Date fechaRegistro;
    private Integer puntosParaSiguienteNivel;
    private String siguienteNivel;
}
