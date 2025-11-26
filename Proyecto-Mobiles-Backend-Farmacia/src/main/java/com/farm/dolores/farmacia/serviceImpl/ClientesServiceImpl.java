package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.ClientesDao;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.service.ClientesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientesServiceImpl implements ClientesService {

    @Autowired
    private ClientesDao clientesDao;

    @Override
    public Clientes create(Clientes clientes) {
        return clientesDao.create(clientes);
    }

    @Override
    public Clientes update(Clientes clientes) {
        return clientesDao.update(clientes);
    }

    @Override
    public void delete(Long id) {
        clientesDao.delete(id);
    }

    @Override
    public Optional<Clientes> read(Long id) {
        return clientesDao.read(id);
    }

    @Override
    public List<Clientes> readAll() {
        return clientesDao.readAll();
    }
}
