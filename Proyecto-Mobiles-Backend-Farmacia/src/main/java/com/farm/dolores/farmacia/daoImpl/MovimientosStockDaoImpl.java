package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.MovimientosStockDao;
import com.farm.dolores.farmacia.entity.MovimientosStock;
import com.farm.dolores.farmacia.repository.MovimientosStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MovimientosStockDaoImpl implements MovimientosStockDao {

    @Autowired
    private MovimientosStockRepository movimientosstockRepository;

    @Override
    public MovimientosStock create(MovimientosStock movimientosstock) {
        return movimientosstockRepository.save(movimientosstock);
    }

    @Override
    public MovimientosStock update(MovimientosStock movimientosstock) {
        return movimientosstockRepository.save(movimientosstock);
    }

    @Override
    public void delete(Long id) {
        movimientosstockRepository.deleteById(id);
    }

    @Override
    public Optional<MovimientosStock> read(Long id) {
        return movimientosstockRepository.findById(id);
    }

    @Override
    public List<MovimientosStock> readAll() {
        return movimientosstockRepository.findAll();
    }
}
