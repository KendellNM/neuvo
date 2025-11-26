package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.ProductosDao;
import com.farm.dolores.farmacia.entity.Productos;
import com.farm.dolores.farmacia.service.ProductosService;
import com.farm.dolores.farmacia.repository.ProductosRepository;
import com.farm.dolores.farmacia.repository.spec.ProductosSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class ProductosServiceImpl implements ProductosService {

    @Autowired
    private ProductosDao productosDao;

    @Autowired
    private ProductosRepository productosRepository;

    @Override
    public Productos create(Productos productos) {
        return productosDao.create(productos);
    }

    @Override
    public Productos update(Productos productos) {
        return productosDao.update(productos);
    }

    @Override
    public void delete(Long id) {
        productosDao.delete(id);
    }

    @Override
    public Optional<Productos> read(Long id) {
        return productosDao.read(id);
    }

    @Override
    public List<Productos> readAll() {
        return productosDao.readAll();
    }

    @Override
    public Page<Productos> catalog(String q, Long categoriaId, Long laboratorioId, Boolean requiereReceta, Pageable pageable) {
        var spec = ProductosSpecifications.build(q, categoriaId, laboratorioId, requiereReceta);
        return productosRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<Productos> findByCodigoBarras(String codigoBarras) {
        return Optional.ofNullable(productosRepository.findByCodigoBarras(codigoBarras));
    }
}
