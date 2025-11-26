package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.ProductosDao;
import com.farm.dolores.farmacia.entity.Productos;
import com.farm.dolores.farmacia.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductosDaoImpl implements ProductosDao {

    @Autowired
    private ProductosRepository productosRepository;

    @Override
    public Productos create(Productos productos) {
        return productosRepository.save(productos);
    }

    @Override
    public Productos update(Productos productos) {
        return productosRepository.save(productos);
    }

    @Override
    public void delete(Long id) {
        productosRepository.deleteById(id);
    }

    @Override
    public Optional<Productos> read(Long id) {
        return productosRepository.findById(id);
    }

    @Override
    public List<Productos> readAll() {
        return productosRepository.findAll();
    }
}
