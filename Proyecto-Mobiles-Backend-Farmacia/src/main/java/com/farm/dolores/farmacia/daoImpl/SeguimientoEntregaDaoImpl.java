package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.SeguimientoEntregaDao;
import com.farm.dolores.farmacia.entity.SeguimientoEntrega;
import com.farm.dolores.farmacia.repository.SeguimientoEntregaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SeguimientoEntregaDaoImpl implements SeguimientoEntregaDao {

    @Autowired
    private SeguimientoEntregaRepository seguimientoentregaRepository;

    @Override
    public SeguimientoEntrega create(SeguimientoEntrega seguimientoentrega) {
        return seguimientoentregaRepository.save(seguimientoentrega);
    }

    @Override
    public SeguimientoEntrega update(SeguimientoEntrega seguimientoentrega) {
        return seguimientoentregaRepository.save(seguimientoentrega);
    }

    @Override
    public void delete(Long id) {
        seguimientoentregaRepository.deleteById(id);
    }

    @Override
    public Optional<SeguimientoEntrega> read(Long id) {
        return seguimientoentregaRepository.findById(id);
    }

    @Override
    public List<SeguimientoEntrega> readAll() {
        return seguimientoentregaRepository.findAll();
    }
}
