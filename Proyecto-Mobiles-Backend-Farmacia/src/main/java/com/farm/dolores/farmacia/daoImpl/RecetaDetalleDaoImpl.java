package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.RecetaDetalleDao;
import com.farm.dolores.farmacia.entity.RecetaDetalle;
import com.farm.dolores.farmacia.repository.RecetaDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RecetaDetalleDaoImpl implements RecetaDetalleDao {

    @Autowired
    private RecetaDetalleRepository recetadetalleRepository;

    @Override
    public RecetaDetalle create(RecetaDetalle recetadetalle) {
        return recetadetalleRepository.save(recetadetalle);
    }

    @Override
    public RecetaDetalle update(RecetaDetalle recetadetalle) {
        return recetadetalleRepository.save(recetadetalle);
    }

    @Override
    public void delete(Long id) {
        recetadetalleRepository.deleteById(id);
    }

    @Override
    public Optional<RecetaDetalle> read(Long id) {
        return recetadetalleRepository.findById(id);
    }

    @Override
    public List<RecetaDetalle> readAll() {
        return recetadetalleRepository.findAll();
    }
}
