package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.LaboratoriosDao;
import com.farm.dolores.farmacia.entity.Laboratorios;
import com.farm.dolores.farmacia.repository.LaboratoriosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LaboratoriosDaoImpl implements LaboratoriosDao {

    @Autowired
    private LaboratoriosRepository laboratoriosRepository;

    @Override
    public Laboratorios create(Laboratorios laboratorios) {
        return laboratoriosRepository.save(laboratorios);
    }

    @Override
    public Laboratorios update(Laboratorios laboratorios) {
        return laboratoriosRepository.save(laboratorios);
    }

    @Override
    public void delete(Long id) {
        laboratoriosRepository.deleteById(id);
    }

    @Override
    public Optional<Laboratorios> read(Long id) {
        return laboratoriosRepository.findById(id);
    }

    @Override
    public List<Laboratorios> readAll() {
        return laboratoriosRepository.findAll();
    }
}
