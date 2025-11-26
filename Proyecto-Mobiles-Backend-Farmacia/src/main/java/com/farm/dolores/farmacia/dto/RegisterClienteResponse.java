package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterClienteResponse {
    private Long usuarioId;
    private Long clienteId;
    private String usuario;
    private String correo;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String token;
    private List<String> roles;
}
