package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecetaResponse {
    private Long idRecetas;
    private Date fecha_emision;
    private Date fecha_vencimiento;
    private String diagnostico;
    private String observaciones;
    private String estado;
    private String numero_receta;

    private UsuarioSummary usuarios;
    private ClienteSummary clientes;
    private MedicoSummary medicos;
}
