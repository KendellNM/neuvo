package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecetaRequest {
    private Long clienteId;
    private Long medicoId;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private String diagnostico;
    private String observaciones;
    private String numeroReceta;
}
