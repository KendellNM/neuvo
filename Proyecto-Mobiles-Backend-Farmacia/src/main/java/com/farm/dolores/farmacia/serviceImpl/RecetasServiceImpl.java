package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.RecetasDao;
import com.farm.dolores.farmacia.entity.Recetas;
import com.farm.dolores.farmacia.service.RecetasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecetasServiceImpl implements RecetasService {

    @Autowired
    private RecetasDao recetasDao;

    @Override
    public Recetas create(Recetas recetas) {
        return recetasDao.create(recetas);
    }

    @Override
    public Recetas update(Recetas recetas) {
        return recetasDao.update(recetas);
    }

    @Override
    public void delete(Long id) {
        recetasDao.delete(id);
    }

    @Override
    public Optional<Recetas> read(Long id) {
        return recetasDao.read(id);
    }

    @Override
    public List<Recetas> readAll() {
        return recetasDao.readAll();
    }
}
