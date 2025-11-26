package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.MovimientosStock;

import java.util.List;
import java.util.Optional;

public interface MovimientosStockDao {

    MovimientosStock create(MovimientosStock movimientosstock);

    MovimientosStock update(MovimientosStock movimientosstock);

    void delete(Long id);

    Optional<MovimientosStock> read(Long id);

    List<MovimientosStock> readAll();
}
