package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaDao {

    Categoria create(Categoria categoria);

    Categoria update(Categoria categoria);

    void delete(Long id);

    Optional<Categoria> read(Long id);

    List<Categoria> readAll();
}
