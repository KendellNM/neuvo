package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.Direcciones;

import java.util.List;
import java.util.Optional;

public interface DireccionesService {

    Direcciones create(Direcciones direcciones);

    Direcciones update(Direcciones direcciones);

    void delete(Long id);

    Optional<Direcciones> read(Long id);

    List<Direcciones> readAll();
}
