package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.CategoriaDao;
import com.farm.dolores.farmacia.entity.Categoria;
import com.farm.dolores.farmacia.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoriaDaoImpl implements CategoriaDao {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public Categoria create(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria update(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public void delete(Long id) {
        categoriaRepository.deleteById(id);
    }

    @Override
    public Optional<Categoria> read(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public List<Categoria> readAll() {
        return categoriaRepository.findAll();
    }
}
