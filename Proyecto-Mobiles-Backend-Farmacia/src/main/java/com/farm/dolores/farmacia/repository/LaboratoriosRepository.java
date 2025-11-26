package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Laboratorios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratoriosRepository extends JpaRepository<Laboratorios, Long> {
}
