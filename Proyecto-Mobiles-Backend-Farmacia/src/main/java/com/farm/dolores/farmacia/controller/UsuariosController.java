package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/usuarios")
@Tag(name = "👥 Usuarios", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "bearer-jwt")
public class UsuariosController {

    @Autowired
    private UsuariosService usuariosService;

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
