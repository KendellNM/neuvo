package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.ConsultaOnline;

import java.util.List;
import java.util.Optional;

public interface ConsultaOnlineDao {

    ConsultaOnline create(ConsultaOnline consultaonline);

    ConsultaOnline update(ConsultaOnline consultaonline);

    void delete(Long id);

    Optional<ConsultaOnline> read(Long id);

    List<ConsultaOnline> readAll();
}
