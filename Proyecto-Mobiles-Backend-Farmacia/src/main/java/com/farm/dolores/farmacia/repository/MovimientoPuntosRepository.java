package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.MovimientoPuntos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoPuntosRepository extends JpaRepository<MovimientoPuntos, Long> {
    List<MovimientoPuntos> findByProgramaFidelizacionIdProgramaFidelizacionOrderByFechaDesc(Long programaId);
}
