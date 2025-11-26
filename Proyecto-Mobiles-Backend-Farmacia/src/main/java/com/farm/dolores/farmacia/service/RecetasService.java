package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.Recetas;

import java.util.List;
import java.util.Optional;

public interface RecetasService {

    Recetas create(Recetas recetas);

    Recetas update(Recetas recetas);

    void delete(Long id);

    Optional<Recetas> read(Long id);

    List<Recetas> readAll();
}
