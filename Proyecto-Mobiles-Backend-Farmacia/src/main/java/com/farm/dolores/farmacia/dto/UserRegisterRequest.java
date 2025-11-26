package com.farm.dolores.farmacia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    // Datos base de usuario
    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String contrasena;
    private String estado; // activo/inactivo

    // Si no envían este campo, asumimos false
    private boolean esCliente;

    // Datos del cliente (requeridos solo si esCliente = true)
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String fechaNacimiento; // ISO-8601 (yyyy-MM-dd) o similar
    private String genero;
}
