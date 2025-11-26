package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.EntregasDao;
import com.farm.dolores.farmacia.entity.Entregas;
import com.farm.dolores.farmacia.service.EntregasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntregasServiceImpl implements EntregasService {

    @Autowired
    private EntregasDao entregasDao;

    @Override
    public Entregas create(Entregas entregas) {
        return entregasDao.create(entregas);
    }

    @Override
    public Entregas update(Entregas entregas) {
        return entregasDao.update(entregas);
    }

    @Override
    public void delete(Long id) {
        entregasDao.delete(id);
    }

    @Override
    public Optional<Entregas> read(Long id) {
        return entregasDao.read(id);
    }

    @Override
    public List<Entregas> readAll() {
        return entregasDao.readAll();
    }
}
