package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.PedidoDetalleDao;
import com.farm.dolores.farmacia.entity.PedidoDetalle;
import com.farm.dolores.farmacia.service.PedidoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoDetalleServiceImpl implements PedidoDetalleService {

    @Autowired
    private PedidoDetalleDao pedidodetalleDao;

    @Override
    public PedidoDetalle create(PedidoDetalle pedidodetalle) {
        return pedidodetalleDao.create(pedidodetalle);
    }

    @Override
    public PedidoDetalle update(PedidoDetalle pedidodetalle) {
        return pedidodetalleDao.update(pedidodetalle);
    }

    @Override
    public void delete(Long id) {
        pedidodetalleDao.delete(id);
    }

    @Override
    public Optional<PedidoDetalle> read(Long id) {
        return pedidodetalleDao.read(id);
    }

    @Override
    public List<PedidoDetalle> readAll() {
        return pedidodetalleDao.readAll();
    }
}
