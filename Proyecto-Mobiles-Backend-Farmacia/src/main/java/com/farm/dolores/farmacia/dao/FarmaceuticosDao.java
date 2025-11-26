package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Farmaceuticos;

import java.util.List;
import java.util.Optional;

public interface FarmaceuticosDao {

    Farmaceuticos create(Farmaceuticos farmaceuticos);

    Farmaceuticos update(Farmaceuticos farmaceuticos);

    void delete(Long id);

    Optional<Farmaceuticos> read(Long id);

    List<Farmaceuticos> readAll();
}
