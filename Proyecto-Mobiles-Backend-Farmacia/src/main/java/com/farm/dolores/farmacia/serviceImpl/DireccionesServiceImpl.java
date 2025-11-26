package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.DireccionesDao;
import com.farm.dolores.farmacia.entity.Direcciones;
import com.farm.dolores.farmacia.service.DireccionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionesServiceImpl implements DireccionesService {

    @Autowired
    private DireccionesDao direccionesDao;

    @Override
    public Direcciones create(Direcciones direcciones) {
        return direccionesDao.create(direcciones);
    }

    @Override
    public Direcciones update(Direcciones direcciones) {
        return direccionesDao.update(direcciones);
    }

    @Override
    public void delete(Long id) {
        direccionesDao.delete(id);
    }

    @Override
    public Optional<Direcciones> read(Long id) {
        return direccionesDao.read(id);
    }

    @Override
    public List<Direcciones> readAll() {
        return direccionesDao.readAll();
    }
}
