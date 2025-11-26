package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.MedicosDao;
import com.farm.dolores.farmacia.entity.Medicos;
import com.farm.dolores.farmacia.service.MedicosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicosServiceImpl implements MedicosService {

    @Autowired
    private MedicosDao medicosDao;

    @Override
    public Medicos create(Medicos medicos) {
        return medicosDao.create(medicos);
    }

    @Override
    public Medicos update(Medicos medicos) {
        return medicosDao.update(medicos);
    }

    @Override
    public void delete(Long id) {
        medicosDao.delete(id);
    }

    @Override
    public Optional<Medicos> read(Long id) {
        return medicosDao.read(id);
    }

    @Override
    public List<Medicos> readAll() {
        return medicosDao.readAll();
    }
}
