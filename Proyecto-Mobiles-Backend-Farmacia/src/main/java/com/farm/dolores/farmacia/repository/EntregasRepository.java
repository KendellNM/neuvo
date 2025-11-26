package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Entregas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregasRepository extends JpaRepository<Entregas, Long> {
}
