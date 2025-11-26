package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.dto.RegisterClienteRequest;
import com.farm.dolores.farmacia.dto.RegisterClienteResponse;

public interface RegistroClienteService {
    RegisterClienteResponse registerCliente(RegisterClienteRequest request);
}
