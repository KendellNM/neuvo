package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.NotificacionesDao;
import com.farm.dolores.farmacia.entity.Notificaciones;
import com.farm.dolores.farmacia.repository.NotificacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NotificacionesDaoImpl implements NotificacionesDao {

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Override
    public Notificaciones create(Notificaciones notificaciones) {
        return notificacionesRepository.save(notificaciones);
    }

    @Override
    public Notificaciones update(Notificaciones notificaciones) {
        return notificacionesRepository.save(notificaciones);
    }

    @Override
    public void delete(Long id) {
        notificacionesRepository.deleteById(id);
    }

    @Override
    public Optional<Notificaciones> read(Long id) {
        return notificacionesRepository.findById(id);
    }

    @Override
    public List<Notificaciones> readAll() {
        return notificacionesRepository.findAll();
    }
}
