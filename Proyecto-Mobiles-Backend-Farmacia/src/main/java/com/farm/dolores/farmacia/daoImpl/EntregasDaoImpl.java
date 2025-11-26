package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.EntregasDao;
import com.farm.dolores.farmacia.entity.Entregas;
import com.farm.dolores.farmacia.repository.EntregasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EntregasDaoImpl implements EntregasDao {

    @Autowired
    private EntregasRepository entregasRepository;

    @Override
    public Entregas create(Entregas entregas) {
        return entregasRepository.save(entregas);
    }

    @Override
    public Entregas update(Entregas entregas) {
        return entregasRepository.save(entregas);
    }

    @Override
    public void delete(Long id) {
        entregasRepository.deleteById(id);
    }

    @Override
    public Optional<Entregas> read(Long id) {
        return entregasRepository.findById(id);
    }

    @Override
    public List<Entregas> readAll() {
        return entregasRepository.findAll();
    }
}
