package com.farm.dolores.farmacia.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String usuario;
    private String correo;
    private String password;
}
