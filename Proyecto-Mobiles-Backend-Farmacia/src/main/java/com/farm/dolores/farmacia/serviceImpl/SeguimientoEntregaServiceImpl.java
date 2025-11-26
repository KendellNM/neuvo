package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.SeguimientoEntregaDao;
import com.farm.dolores.farmacia.entity.SeguimientoEntrega;
import com.farm.dolores.farmacia.service.SeguimientoEntregaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeguimientoEntregaServiceImpl implements SeguimientoEntregaService {

    @Autowired
    private SeguimientoEntregaDao seguimientoentregaDao;

    @Override
    public SeguimientoEntrega create(SeguimientoEntrega seguimientoentrega) {
        return seguimientoentregaDao.create(seguimientoentrega);
    }

    @Override
    public SeguimientoEntrega update(SeguimientoEntrega seguimientoentrega) {
        return seguimientoentregaDao.update(seguimientoentrega);
    }

    @Override
    public void delete(Long id) {
        seguimientoentregaDao.delete(id);
    }

    @Override
    public Optional<SeguimientoEntrega> read(Long id) {
        return seguimientoentregaDao.read(id);
    }

    @Override
    public List<SeguimientoEntrega> readAll() {
        return seguimientoentregaDao.readAll();
    }
}
