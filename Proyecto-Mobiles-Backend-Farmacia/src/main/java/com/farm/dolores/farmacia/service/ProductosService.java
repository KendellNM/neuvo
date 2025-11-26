package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.Productos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductosService {

    Productos create(Productos productos);

    Productos update(Productos productos);

    void delete(Long id);

    Optional<Productos> read(Long id);

    List<Productos> readAll();

    Page<Productos> catalog(String q, Long categoriaId, Long laboratorioId, Boolean requiereReceta, Pageable pageable);

    Optional<Productos> findByCodigoBarras(String codigoBarras);
}
