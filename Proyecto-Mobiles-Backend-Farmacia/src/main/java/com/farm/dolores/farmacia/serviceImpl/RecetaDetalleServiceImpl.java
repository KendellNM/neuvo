package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.RecetaDetalleDao;
import com.farm.dolores.farmacia.entity.RecetaDetalle;
import com.farm.dolores.farmacia.service.RecetaDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecetaDetalleServiceImpl implements RecetaDetalleService {

    @Autowired
    private RecetaDetalleDao recetadetalleDao;

    @Override
    public RecetaDetalle create(RecetaDetalle recetadetalle) {
        return recetadetalleDao.create(recetadetalle);
    }

    @Override
    public RecetaDetalle update(RecetaDetalle recetadetalle) {
        return recetadetalleDao.update(recetadetalle);
    }

    @Override
    public void delete(Long id) {
        recetadetalleDao.delete(id);
    }

    @Override
    public Optional<RecetaDetalle> read(Long id) {
        return recetadetalleDao.read(id);
    }

    @Override
    public List<RecetaDetalle> readAll() {
        return recetadetalleDao.readAll();
    }
}
