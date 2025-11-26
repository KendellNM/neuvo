package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, Long>, JpaSpecificationExecutor<Productos> {
    Productos findByCodigoBarras(String codigoBarras);
}
