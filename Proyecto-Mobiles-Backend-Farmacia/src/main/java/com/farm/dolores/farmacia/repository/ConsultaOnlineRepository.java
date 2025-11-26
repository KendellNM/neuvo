package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.ConsultaOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultaOnlineRepository extends JpaRepository<ConsultaOnline, Long> {
}
