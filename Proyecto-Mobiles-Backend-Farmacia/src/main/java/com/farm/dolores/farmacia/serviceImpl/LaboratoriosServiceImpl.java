package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.LaboratoriosDao;
import com.farm.dolores.farmacia.entity.Laboratorios;
import com.farm.dolores.farmacia.service.LaboratoriosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaboratoriosServiceImpl implements LaboratoriosService {

    @Autowired
    private LaboratoriosDao laboratoriosDao;

    @Override
    public Laboratorios create(Laboratorios laboratorios) {
        return laboratoriosDao.create(laboratorios);
    }

    @Override
    public Laboratorios update(Laboratorios laboratorios) {
        return laboratoriosDao.update(laboratorios);
    }

    @Override
    public void delete(Long id) {
        laboratoriosDao.delete(id);
    }

    @Override
    public Optional<Laboratorios> read(Long id) {
        return laboratoriosDao.read(id);
    }

    @Override
    public List<Laboratorios> readAll() {
        return laboratoriosDao.readAll();
    }
}
