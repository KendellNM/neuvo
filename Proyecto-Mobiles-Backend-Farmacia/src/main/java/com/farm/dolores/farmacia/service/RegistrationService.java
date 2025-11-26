package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.dto.UserRegisterRequest;
import com.farm.dolores.farmacia.entity.Usuarios;

public interface RegistrationService {
    Usuarios register(UserRegisterRequest request);
}
