package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.Repartidores;

import java.util.List;
import java.util.Optional;

public interface RepartidoresService {

    Repartidores create(Repartidores repartidores);

    Repartidores update(Repartidores repartidores);

    void delete(Long id);

    Optional<Repartidores> read(Long id);

    List<Repartidores> readAll();
}
