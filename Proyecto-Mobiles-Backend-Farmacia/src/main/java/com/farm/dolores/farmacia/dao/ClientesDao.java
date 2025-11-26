package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Clientes;

import java.util.List;
import java.util.Optional;

public interface ClientesDao {

    Clientes create(Clientes clientes);

    Clientes update(Clientes clientes);

    void delete(Long id);

    Optional<Clientes> read(Long id);

    List<Clientes> readAll();
}
