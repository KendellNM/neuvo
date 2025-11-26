package com.farm.dolores.farmacia.dto;

import lombok.Data;

@Data
public class RegisterClienteRequest {
    private String usuario;
    private String correo;
    private String password;

    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String genero;
    private String fechaNacimiento; // (yyyy-MM-dd)
}
