package com.farm.dolores.farmacia.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String correo; // esto va para solicitar reset
}
