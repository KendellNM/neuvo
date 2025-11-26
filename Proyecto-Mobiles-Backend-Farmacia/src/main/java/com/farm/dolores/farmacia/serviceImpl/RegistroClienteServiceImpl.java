package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dto.RegisterClienteRequest;
import com.farm.dolores.farmacia.dto.RegisterClienteResponse;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.entity.Roles;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.repository.ClientesRepository;
import com.farm.dolores.farmacia.repository.UsuariosRepository;
import com.farm.dolores.farmacia.repository.RolesRepository;
import com.farm.dolores.farmacia.repository.UsuarioRolRepository;
import com.farm.dolores.farmacia.security.JwtUtil;
import com.farm.dolores.farmacia.service.RegistroClienteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class RegistroClienteServiceImpl implements RegistroClienteService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public RegisterClienteResponse registerCliente(RegisterClienteRequest request) {
        // Validaciones básicas
        if (request.getCorreo() == null || request.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El campo 'correo' es obligatorio");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("El campo 'password' es obligatorio");
        }
        if (usuariosRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new IllegalStateException("Correo ya registrado");
        }

        // Crear Usuario
        Usuarios u = new Usuarios();
        // 'usuario' es opcional; puede ser null o vacío
        u.setUsuario(request.getUsuario());
        u.setCorreo(request.getCorreo());
        u.setContrasena(passwordEncoder.encode(request.getPassword()));
        u.setEstado("ACTIVO");
        u.setFecha_creacion(new Date());
        usuariosRepository.save(u);

        // Crear Cliente asociado
        Clientes c = new Clientes();
        c.setNombres(request.getNombres());
        c.setApellidos(request.getApellidos());
        c.setDni(request.getDni());
        c.setTelefono(request.getTelefono());
        c.setGenero(request.getGenero());
        c.setEstado("ACTIVO");

        if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isBlank()) {
            try {
                Date fn = new SimpleDateFormat("yyyy-MM-dd").parse(request.getFechaNacimiento());
                c.setFecha_nacimiento(fn);
            } catch (ParseException e) {
                throw new IllegalArgumentException("fechaNacimiento debe tener formato yyyy-MM-dd");
            }
        }
        // Nota: El campo en la entidad Clientes se llama 'Clientes' y es de tipo Usuarios
        // por consistencia del modelo existente utilizamos el setter generado por Lombok
        c.setClientes(u);
        clientesRepository.save(c);

        // Asignar rol CLIENTE por defecto
        Roles rolCliente = rolesRepository.findAll().stream()
                .filter(r -> r.getNombre() != null && r.getNombre().equalsIgnoreCase("CLIENTE"))
                .findFirst()
                .orElseGet(() -> {
                    Roles nuevo = new Roles();
                    nuevo.setNombre("CLIENTE");
                    nuevo.setDescripcion("Rol por defecto para clientes");
                    nuevo.setEstado("ACTIVO");
                    return rolesRepository.save(nuevo);
                });

        UsuarioRol ur = new UsuarioRol();
        ur.setUsuarios(u);
        ur.setRoles(rolCliente);
        ur.setEstado("ACTIVO");
        usuarioRolRepository.save(ur);

        // Generar token para el nuevo cliente
        String token = jwtUtil.generateToken(u.getCorreo());
        List<String> roles = List.of(
                (rolCliente.getNombre() != null && rolCliente.getNombre().startsWith("ROLE_"))
                        ? rolCliente.getNombre()
                        : "ROLE_" + rolCliente.getNombre()
        );

        return new RegisterClienteResponse(
                u.getIdUsuarios(),
                c.getIdClientes(),
                u.getUsuario(),
                u.getCorreo(),
                c.getNombres(),
                c.getApellidos(),
                c.getDni(),
                c.getTelefono(),
                token,
                roles
        );
    }
}
