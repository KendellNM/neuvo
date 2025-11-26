package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.FarmaceuticosDao;
import com.farm.dolores.farmacia.entity.Farmaceuticos;
import com.farm.dolores.farmacia.service.FarmaceuticosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FarmaceuticosServiceImpl implements FarmaceuticosService {

    @Autowired
    private FarmaceuticosDao farmaceuticosDao;

    @Override
    public Farmaceuticos create(Farmaceuticos farmaceuticos) {
        return farmaceuticosDao.create(farmaceuticos);
    }

    @Override
    public Farmaceuticos update(Farmaceuticos farmaceuticos) {
        return farmaceuticosDao.update(farmaceuticos);
    }

    @Override
    public void delete(Long id) {
        farmaceuticosDao.delete(id);
    }

    @Override
    public Optional<Farmaceuticos> read(Long id) {
        return farmaceuticosDao.read(id);
    }

    @Override
    public List<Farmaceuticos> readAll() {
        return farmaceuticosDao.readAll();
    }
}
