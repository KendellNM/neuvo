package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.RecetaDetalle;

import java.util.List;
import java.util.Optional;

public interface RecetaDetalleService {

    RecetaDetalle create(RecetaDetalle recetadetalle);

    RecetaDetalle update(RecetaDetalle recetadetalle);

    void delete(Long id);

    Optional<RecetaDetalle> read(Long id);

    List<RecetaDetalle> readAll();
}
