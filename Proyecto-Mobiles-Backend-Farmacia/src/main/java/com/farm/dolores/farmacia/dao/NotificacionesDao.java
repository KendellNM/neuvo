package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Notificaciones;

import java.util.List;
import java.util.Optional;

public interface NotificacionesDao {

    Notificaciones create(Notificaciones notificaciones);

    Notificaciones update(Notificaciones notificaciones);

    void delete(Long id);

    Optional<Notificaciones> read(Long id);

    List<Notificaciones> readAll();
}
