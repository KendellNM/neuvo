package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Medicos;

import java.util.List;
import java.util.Optional;

public interface MedicosDao {

    Medicos create(Medicos medicos);

    Medicos update(Medicos medicos);

    void delete(Long id);

    Optional<Medicos> read(Long id);

    List<Medicos> readAll();
}
