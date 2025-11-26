package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.CategoriaDao;
import com.farm.dolores.farmacia.entity.Categoria;
import com.farm.dolores.farmacia.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaDao categoriaDao;

    @Override
    public Categoria create(Categoria categoria) {
        return categoriaDao.create(categoria);
    }

    @Override
    public Categoria update(Categoria categoria) {
        return categoriaDao.update(categoria);
    }

    @Override
    public void delete(Long id) {
        categoriaDao.delete(id);
    }

    @Override
    public Optional<Categoria> read(Long id) {
        return categoriaDao.read(id);
    }

    @Override
    public List<Categoria> readAll() {
        return categoriaDao.readAll();
    }
}
