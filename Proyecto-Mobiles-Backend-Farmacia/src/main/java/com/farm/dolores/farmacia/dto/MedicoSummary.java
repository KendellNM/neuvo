package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicoSummary {
    private Long id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String telefono;
}
