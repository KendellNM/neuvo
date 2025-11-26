package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.MovimientosStockDao;
import com.farm.dolores.farmacia.entity.MovimientosStock;
import com.farm.dolores.farmacia.service.MovimientosStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovimientosStockServiceImpl implements MovimientosStockService {

    @Autowired
    private MovimientosStockDao movimientosstockDao;

    @Override
    public MovimientosStock create(MovimientosStock movimientosstock) {
        return movimientosstockDao.create(movimientosstock);
    }

    @Override
    public MovimientosStock update(MovimientosStock movimientosstock) {
        return movimientosstockDao.update(movimientosstock);
    }

    @Override
    public void delete(Long id) {
        movimientosstockDao.delete(id);
    }

    @Override
    public Optional<MovimientosStock> read(Long id) {
        return movimientosstockDao.read(id);
    }

    @Override
    public List<MovimientosStock> readAll() {
        return movimientosstockDao.readAll();
    }
}
