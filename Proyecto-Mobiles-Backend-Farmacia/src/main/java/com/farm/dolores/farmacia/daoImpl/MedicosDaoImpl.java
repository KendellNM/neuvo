package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.MedicosDao;
import com.farm.dolores.farmacia.entity.Medicos;
import com.farm.dolores.farmacia.repository.MedicosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MedicosDaoImpl implements MedicosDao {

    @Autowired
    private MedicosRepository medicosRepository;

    @Override
    public Medicos create(Medicos medicos) {
        return medicosRepository.save(medicos);
    }

    @Override
    public Medicos update(Medicos medicos) {
        return medicosRepository.save(medicos);
    }

    @Override
    public void delete(Long id) {
        medicosRepository.deleteById(id);
    }

    @Override
    public Optional<Medicos> read(Long id) {
        return medicosRepository.findById(id);
    }

    @Override
    public List<Medicos> readAll() {
        return medicosRepository.findAll();
    }
}
