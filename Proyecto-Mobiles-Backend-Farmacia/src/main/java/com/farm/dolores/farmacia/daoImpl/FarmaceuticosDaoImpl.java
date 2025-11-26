package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.FarmaceuticosDao;
import com.farm.dolores.farmacia.entity.Farmaceuticos;
import com.farm.dolores.farmacia.repository.FarmaceuticosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FarmaceuticosDaoImpl implements FarmaceuticosDao {

    @Autowired
    private FarmaceuticosRepository farmaceuticosRepository;

    @Override
    public Farmaceuticos create(Farmaceuticos farmaceuticos) {
        return farmaceuticosRepository.save(farmaceuticos);
    }

    @Override
    public Farmaceuticos update(Farmaceuticos farmaceuticos) {
        return farmaceuticosRepository.save(farmaceuticos);
    }

    @Override
    public void delete(Long id) {
        farmaceuticosRepository.deleteById(id);
    }

    @Override
    public Optional<Farmaceuticos> read(Long id) {
        return farmaceuticosRepository.findById(id);
    }

    @Override
    public List<Farmaceuticos> readAll() {
        return farmaceuticosRepository.findAll();
    }
}
