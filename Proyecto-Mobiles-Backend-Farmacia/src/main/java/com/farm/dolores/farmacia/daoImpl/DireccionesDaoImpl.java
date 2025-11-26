package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.DireccionesDao;
import com.farm.dolores.farmacia.entity.Direcciones;
import com.farm.dolores.farmacia.repository.DireccionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DireccionesDaoImpl implements DireccionesDao {

    @Autowired
    private DireccionesRepository direccionesRepository;

    @Override
    public Direcciones create(Direcciones direcciones) {
        return direccionesRepository.save(direcciones);
    }

    @Override
    public Direcciones update(Direcciones direcciones) {
        return direccionesRepository.save(direcciones);
    }

    @Override
    public void delete(Long id) {
        direccionesRepository.deleteById(id);
    }

    @Override
    public Optional<Direcciones> read(Long id) {
        return direccionesRepository.findById(id);
    }

    @Override
    public List<Direcciones> readAll() {
        return direccionesRepository.findAll();
    }
}
