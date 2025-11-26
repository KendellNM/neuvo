package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dto.UserRegisterRequest;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.service.ClientesService;
import com.farm.dolores.farmacia.service.RegistrationService;
import com.farm.dolores.farmacia.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private UsuariosService usuariosService;

    @Autowired
    private ClientesService clientesService;

    @Override
    @Transactional
    public Usuarios register(UserRegisterRequest request) {
        // Validaciones básicas
        if (request.getCorreo() == null || request.getCorreo().isBlank()) {
            throw new IllegalArgumentException("correo es obligatorio");
        }
        if (request.getContrasena() == null || request.getContrasena().isBlank()) {
            throw new IllegalArgumentException("contrasena es obligatoria");
        }

        // Crear usuario base
        Usuarios nuevo = new Usuarios();
        nuevo.setCorreo(request.getCorreo());
        nuevo.setContrasena(request.getContrasena());
        nuevo.setEstado(request.getEstado());
        // usuario = correo
        nuevo.setUsuario(request.getCorreo());

        Usuarios creado = usuariosService.create(nuevo);

        if (request.isEsCliente()) {
            Clientes cli = new Clientes();
            cli.setNombres(request.getNombres());
            cli.setApellidos(request.getApellidos());
            cli.setDni(request.getDni());
            cli.setTelefono(request.getTelefono());
            cli.setGenero(request.getGenero());
            cli.setEstado("activo");

            if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isBlank()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cli.setFecha_nacimiento(sdf.parse(request.getFechaNacimiento()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException("fechaNacimiento debe tener formato yyyy-MM-dd");
                }
            }

            // Relacionar con el usuario (FK vive en Clientes)
            cli.setClientes(creado);
            clientesService.create(cli);
        }

        return creado;
    }
}
