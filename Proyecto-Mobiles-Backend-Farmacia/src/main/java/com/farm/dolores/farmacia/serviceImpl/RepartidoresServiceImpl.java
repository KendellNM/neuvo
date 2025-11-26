package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.RepartidoresDao;
import com.farm.dolores.farmacia.entity.Repartidores;
import com.farm.dolores.farmacia.service.RepartidoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepartidoresServiceImpl implements RepartidoresService {

    @Autowired
    private RepartidoresDao repartidoresDao;

    @Override
    public Repartidores create(Repartidores repartidores) {
        return repartidoresDao.create(repartidores);
    }

    @Override
    public Repartidores update(Repartidores repartidores) {
        return repartidoresDao.update(repartidores);
    }

    @Override
    public void delete(Long id) {
        repartidoresDao.delete(id);
    }

    @Override
    public Optional<Repartidores> read(Long id) {
        return repartidoresDao.read(id);
    }

    @Override
    public List<Repartidores> readAll() {
        return repartidoresDao.readAll();
    }
}
