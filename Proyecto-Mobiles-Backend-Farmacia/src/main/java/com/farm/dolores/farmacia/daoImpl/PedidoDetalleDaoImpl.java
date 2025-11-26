package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.PedidoDetalleDao;
import com.farm.dolores.farmacia.entity.PedidoDetalle;
import com.farm.dolores.farmacia.repository.PedidoDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PedidoDetalleDaoImpl implements PedidoDetalleDao {

    @Autowired
    private PedidoDetalleRepository pedidodetalleRepository;

    @Override
    public PedidoDetalle create(PedidoDetalle pedidodetalle) {
        return pedidodetalleRepository.save(pedidodetalle);
    }

    @Override
    public PedidoDetalle update(PedidoDetalle pedidodetalle) {
        return pedidodetalleRepository.save(pedidodetalle);
    }

    @Override
    public void delete(Long id) {
        pedidodetalleRepository.deleteById(id);
    }

    @Override
    public Optional<PedidoDetalle> read(Long id) {
        return pedidodetalleRepository.findById(id);
    }

    @Override
    public List<PedidoDetalle> readAll() {
        return pedidodetalleRepository.findAll();
    }
}
