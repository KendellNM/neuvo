package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.PedidosDao;
import com.farm.dolores.farmacia.entity.Pedidos;
import com.farm.dolores.farmacia.repository.PedidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PedidosDaoImpl implements PedidosDao {

    @Autowired
    private PedidosRepository pedidosRepository;

    @Override
    public Pedidos create(Pedidos pedidos) {
        return pedidosRepository.save(pedidos);
    }

    @Override
    public Pedidos update(Pedidos pedidos) {
        return pedidosRepository.save(pedidos);
    }

    @Override
    public void delete(Long id) {
        pedidosRepository.deleteById(id);
    }

    @Override
    public Optional<Pedidos> read(Long id) {
        return pedidosRepository.findById(id);
    }

    @Override
    public List<Pedidos> readAll() {
        return pedidosRepository.findAll();
    }
}
