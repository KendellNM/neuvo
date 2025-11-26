package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.RecetaDetalle;

import java.util.List;
import java.util.Optional;

public interface RecetaDetalleDao {

    RecetaDetalle create(RecetaDetalle recetadetalle);

    RecetaDetalle update(RecetaDetalle recetadetalle);

    void delete(Long id);

    Optional<RecetaDetalle> read(Long id);

    List<RecetaDetalle> readAll();
}
