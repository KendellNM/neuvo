package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.ClientesDao;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClientesDaoImpl implements ClientesDao {

    @Autowired
    private ClientesRepository clientesRepository;

    @Override
    public Clientes create(Clientes clientes) {
        return clientesRepository.save(clientes);
    }

    @Override
    public Clientes update(Clientes clientes) {
        return clientesRepository.save(clientes);
    }

    @Override
    public void delete(Long id) {
        clientesRepository.deleteById(id);
    }

    @Override
    public Optional<Clientes> read(Long id) {
        return clientesRepository.findById(id);
    }

    @Override
    public List<Clientes> readAll() {
        return clientesRepository.findAll();
    }
}
