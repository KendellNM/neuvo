package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.ConsultaOnlineDao;
import com.farm.dolores.farmacia.entity.ConsultaOnline;
import com.farm.dolores.farmacia.service.ConsultaOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaOnlineServiceImpl implements ConsultaOnlineService {

    @Autowired
    private ConsultaOnlineDao consultaonlineDao;

    @Override
    public ConsultaOnline create(ConsultaOnline consultaonline) {
        return consultaonlineDao.create(consultaonline);
    }

    @Override
    public ConsultaOnline update(ConsultaOnline consultaonline) {
        return consultaonlineDao.update(consultaonline);
    }

    @Override
    public void delete(Long id) {
        consultaonlineDao.delete(id);
    }

    @Override
    public Optional<ConsultaOnline> read(Long id) {
        return consultaonlineDao.read(id);
    }

    @Override
    public List<ConsultaOnline> readAll() {
        return consultaonlineDao.readAll();
    }
}
