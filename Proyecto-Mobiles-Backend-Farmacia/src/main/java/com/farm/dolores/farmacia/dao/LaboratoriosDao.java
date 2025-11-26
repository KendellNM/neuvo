package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Laboratorios;

import java.util.List;
import java.util.Optional;

public interface LaboratoriosDao {

    Laboratorios create(Laboratorios laboratorios);

    Laboratorios update(Laboratorios laboratorios);

    void delete(Long id);

    Optional<Laboratorios> read(Long id);

    List<Laboratorios> readAll();
}
