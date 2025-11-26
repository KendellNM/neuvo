package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Medicos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicosRepository extends JpaRepository<Medicos, Long> {
}
