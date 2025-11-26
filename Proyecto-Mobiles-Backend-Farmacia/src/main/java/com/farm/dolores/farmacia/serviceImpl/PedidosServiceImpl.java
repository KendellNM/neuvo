package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.PedidosDao;
import com.farm.dolores.farmacia.entity.Pedidos;
import com.farm.dolores.farmacia.service.PedidosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidosServiceImpl implements PedidosService {

    @Autowired
    private PedidosDao pedidosDao;

    @Override
    public Pedidos create(Pedidos pedidos) {
        return pedidosDao.create(pedidos);
    }

    @Override
    public Pedidos update(Pedidos pedidos) {
        return pedidosDao.update(pedidos);
    }

    @Override
    public void delete(Long id) {
        pedidosDao.delete(id);
    }

    @Override
    public Optional<Pedidos> read(Long id) {
        return pedidosDao.read(id);
    }

    @Override
    public List<Pedidos> readAll() {
        return pedidosDao.readAll();
    }
}
