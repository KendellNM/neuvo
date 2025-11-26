package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.MovimientosStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientosStockRepository extends JpaRepository<MovimientosStock, Long> {
}
