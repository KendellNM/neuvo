package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Farmaceuticos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmaceuticosRepository extends JpaRepository<Farmaceuticos, Long> {
}
