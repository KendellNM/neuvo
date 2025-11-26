package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.ConsultaOnlineDao;
import com.farm.dolores.farmacia.entity.ConsultaOnline;
import com.farm.dolores.farmacia.repository.ConsultaOnlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConsultaOnlineDaoImpl implements ConsultaOnlineDao {

    @Autowired
    private ConsultaOnlineRepository consultaonlineRepository;

    @Override
    public ConsultaOnline create(ConsultaOnline consultaonline) {
        return consultaonlineRepository.save(consultaonline);
    }

    @Override
    public ConsultaOnline update(ConsultaOnline consultaonline) {
        return consultaonlineRepository.save(consultaonline);
    }

    @Override
    public void delete(Long id) {
        consultaonlineRepository.deleteById(id);
    }

    @Override
    public Optional<ConsultaOnline> read(Long id) {
        return consultaonlineRepository.findById(id);
    }

    @Override
    public List<ConsultaOnline> readAll() {
        return consultaonlineRepository.findAll();
    }
}
