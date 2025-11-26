package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.repository.UsuariosRepository;
import com.farm.dolores.farmacia.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.farm.dolores.farmacia.repository.RepartidoresRepository;
import com.farm.dolores.farmacia.repository.ClientesRepository;
import com.farm.dolores.farmacia.entity.Repartidores;
import com.farm.dolores.farmacia.entity.Clientes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/usuarios")
@Tag(name = "👥 Usuarios", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "bearer-jwt")
public class UsuariosController {

    @Autowired
    private UsuariosService usuariosService;
    
    @Autowired
    private UsuariosRepository usuariosRepository;
    
    @Autowired
    private RepartidoresRepository repartidoresRepository;
    
    @Autowired
    private ClientesRepository clientesRepository;

    @Operation(summary = "Obtener usuario actual", description = "Devuelve la información del usuario autenticado")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Optional<Usuarios> userOpt = usuariosRepository.findByCorreo(username);
            if (userOpt.isEmpty()) {
                userOpt = usuariosRepository.findByUsuario(username);
            }
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            
            Usuarios user = userOpt.get();
            
            // Crear respuesta con los campos necesarios
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getIdUsuarios());
            response.put("usuario", user.getUsuario());
            response.put("correo", user.getCorreo());
            response.put("estado", user.getEstado());
            
            // Buscar si el usuario es repartidor
            Optional<Repartidores> repartidorOpt = repartidoresRepository.findByUsuario(user);
            if (repartidorOpt.isPresent()) {
                Repartidores repartidor = repartidorOpt.get();
                response.put("repartidorId", repartidor.getIdRepartidores());
                response.put("nombres", repartidor.getNombres());
                response.put("apellidos", repartidor.getApellidos());
            }
            
            // Buscar si el usuario es cliente
            Optional<Clientes> clienteOpt = clientesRepository.findByUsuario(user);
            if (clienteOpt.isPresent()) {
                Clientes cliente = clienteOpt.get();
                response.put("clienteId", cliente.getIdClientes());
                response.put("nombres", cliente.getNombres());
                response.put("apellidos", cliente.getApellidos());
                response.put("telefono", cliente.getTelefono());
                response.put("dni", cliente.getDni());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener usuario");
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuarios>> readAll() {
        try {
            List<Usuarios> usuarioss = usuariosService.readAll();

            if (usuarioss.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(usuarioss, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<Usuarios> create(@Valid @RequestBody Usuarios usuarios) {
        try {
            Usuarios usuariosCreated = usuariosService.create(usuarios);
            return new ResponseEntity<>(usuariosCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> getById(@PathVariable("id") Long id) {
        try {
            Usuarios usuarios = usuariosService.read(id).get();
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Usuarios> delete(@PathVariable("id") Long id) {
        try {
            usuariosService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Usuarios usuarios) {

        Optional<Usuarios> usuariosOptional = usuariosService.read(id);
        if (usuariosOptional.isPresent()) {
            return new ResponseEntity<>(usuariosService.update(usuarios), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
