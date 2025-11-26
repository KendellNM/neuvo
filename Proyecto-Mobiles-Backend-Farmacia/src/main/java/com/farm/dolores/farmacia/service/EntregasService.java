package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.Entregas;

import java.util.List;
import java.util.Optional;

public interface EntregasService {

    Entregas create(Entregas entregas);

    Entregas update(Entregas entregas);

    void delete(Long id);

    Optional<Entregas> read(Long id);

    List<Entregas> readAll();
}
