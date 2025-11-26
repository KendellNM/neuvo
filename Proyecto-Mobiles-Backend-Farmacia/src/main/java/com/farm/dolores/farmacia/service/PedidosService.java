package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.Pedidos;

import java.util.List;
import java.util.Optional;

public interface PedidosService {

    Pedidos create(Pedidos pedidos);

    Pedidos update(Pedidos pedidos);

    void delete(Long id);

    Optional<Pedidos> read(Long id);

    List<Pedidos> readAll();
}
