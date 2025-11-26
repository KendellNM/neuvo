package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Direcciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionesRepository extends JpaRepository<Direcciones, Long> {
}
