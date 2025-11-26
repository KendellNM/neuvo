package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.RepartidoresDao;
import com.farm.dolores.farmacia.entity.Repartidores;
import com.farm.dolores.farmacia.repository.RepartidoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RepartidoresDaoImpl implements RepartidoresDao {

    @Autowired
    private RepartidoresRepository repartidoresRepository;

    @Override
    public Repartidores create(Repartidores repartidores) {
        return repartidoresRepository.save(repartidores);
    }

    @Override
    public Repartidores update(Repartidores repartidores) {
        return repartidoresRepository.save(repartidores);
    }

    @Override
    public void delete(Long id) {
        repartidoresRepository.deleteById(id);
    }

    @Override
    public Optional<Repartidores> read(Long id) {
        return repartidoresRepository.findById(id);
    }

    @Override
    public List<Repartidores> readAll() {
        return repartidoresRepository.findAll();
    }
}
