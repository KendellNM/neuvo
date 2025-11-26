package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.SeguimientoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeguimientoEntregaRepository extends JpaRepository<SeguimientoEntrega, Long> {
}
