package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Recetas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetasRepository extends JpaRepository<Recetas, Long> {
}
