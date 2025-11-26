package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.PedidoDetalle;

import java.util.List;
import java.util.Optional;

public interface PedidoDetalleService {

    PedidoDetalle create(PedidoDetalle pedidodetalle);

    PedidoDetalle update(PedidoDetalle pedidodetalle);

    void delete(Long id);

    Optional<PedidoDetalle> read(Long id);

    List<PedidoDetalle> readAll();
}
