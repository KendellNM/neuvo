package com.farm.dolores.farmacia.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username; // Puede ser DNI (clientes) o correo (admin/staff)
    private String password;
}
