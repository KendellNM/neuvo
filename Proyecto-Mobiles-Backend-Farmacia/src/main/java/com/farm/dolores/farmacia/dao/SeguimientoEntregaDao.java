package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.SeguimientoEntrega;

import java.util.List;
import java.util.Optional;

public interface SeguimientoEntregaDao {

    SeguimientoEntrega create(SeguimientoEntrega seguimientoentrega);

    SeguimientoEntrega update(SeguimientoEntrega seguimientoentrega);

    void delete(Long id);

    Optional<SeguimientoEntrega> read(Long id);

    List<SeguimientoEntrega> readAll();
}
