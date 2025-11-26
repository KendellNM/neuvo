package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.RecetasDao;
import com.farm.dolores.farmacia.entity.Recetas;
import com.farm.dolores.farmacia.repository.RecetasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RecetasDaoImpl implements RecetasDao {

    @Autowired
    private RecetasRepository recetasRepository;

    @Override
    public Recetas create(Recetas recetas) {
        return recetasRepository.save(recetas);
    }

    @Override
    public Recetas update(Recetas recetas) {
        return recetasRepository.save(recetas);
    }

    @Override
    public void delete(Long id) {
        recetasRepository.deleteById(id);
    }

    @Override
    public Optional<Recetas> read(Long id) {
        return recetasRepository.findById(id);
    }

    @Override
    public List<Recetas> readAll() {
        return recetasRepository.findAll();
    }
}
