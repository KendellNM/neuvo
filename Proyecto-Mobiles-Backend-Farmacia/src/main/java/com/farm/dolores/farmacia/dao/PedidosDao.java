package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Pedidos;

import java.util.List;
import java.util.Optional;

public interface PedidosDao {

    Pedidos create(Pedidos pedidos);

    Pedidos update(Pedidos pedidos);

    void delete(Long id);

    Optional<Pedidos> read(Long id);

    List<Pedidos> readAll();
}
