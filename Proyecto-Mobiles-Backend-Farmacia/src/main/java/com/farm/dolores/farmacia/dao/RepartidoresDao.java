package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Repartidores;

import java.util.List;
import java.util.Optional;

public interface RepartidoresDao {

    Repartidores create(Repartidores repartidores);

    Repartidores update(Repartidores repartidores);

    void delete(Long id);

    Optional<Repartidores> read(Long id);

    List<Repartidores> readAll();
}
