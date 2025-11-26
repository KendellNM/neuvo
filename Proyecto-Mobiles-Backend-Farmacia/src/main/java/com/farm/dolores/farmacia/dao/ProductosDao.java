package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Productos;

import java.util.List;
import java.util.Optional;

public interface ProductosDao {

    Productos create(Productos productos);

    Productos update(Productos productos);

    void delete(Long id);

    Optional<Productos> read(Long id);

    List<Productos> readAll();
}
