package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.RecetaDigitalDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaDigitalDetalleRepository extends JpaRepository<RecetaDigitalDetalle, Long> {
}
