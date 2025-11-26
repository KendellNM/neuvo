package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.NotificacionesDao;
import com.farm.dolores.farmacia.entity.Notificaciones;
import com.farm.dolores.farmacia.service.NotificacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionesServiceImpl implements NotificacionesService {

    @Autowired
    private NotificacionesDao notificacionesDao;

    @Override
    public Notificaciones create(Notificaciones notificaciones) {
        return notificacionesDao.create(notificaciones);
    }

    @Override
    public Notificaciones update(Notificaciones notificaciones) {
        return notificacionesDao.update(notificaciones);
    }

    @Override
    public void delete(Long id) {
        notificacionesDao.delete(id);
    }

    @Override
    public Optional<Notificaciones> read(Long id) {
        return notificacionesDao.read(id);
    }

    @Override
    public List<Notificaciones> readAll() {
        return notificacionesDao.readAll();
    }
}
