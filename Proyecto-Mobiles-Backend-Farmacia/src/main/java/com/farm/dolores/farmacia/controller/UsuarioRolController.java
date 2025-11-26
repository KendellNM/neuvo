package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.service.UsuarioRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/usuariorol")
@Tag(name = "🔗 Usuario-Rol", description = "Asignación de roles a usuarios")
@SecurityRequirement(name = "bearer-jwt")
public class UsuarioRolController {

    @Autowired
    private UsuarioRolService usuariorolService;

    @GetMapping
    public ResponseEntity<List<UsuarioRol>> readAll() {
        try {
            List<UsuarioRol> usuariorols = usuariorolService.readAll();

            if (usuariorols.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(usuariorols, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<UsuarioRol> create(@Valid @RequestBody UsuarioRol usuariorol) {
        try {
            UsuarioRol usuariorolCreated = usuariorolService.create(usuariorol);
            return new ResponseEntity<>(usuariorolCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRol> getById(@PathVariable("id") Long id) {
        try {
            UsuarioRol usuariorol = usuariorolService.read(id).get();
            return new ResponseEntity<>(usuariorol, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioRol> delete(@PathVariable("id") Long id) {
        try {
            usuariorolService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody UsuarioRol usuariorol) {

        Optional<UsuarioRol> usuariorolOptional = usuariorolService.read(id);
        if (usuariorolOptional.isPresent()) {
            return new ResponseEntity<>(usuariorolService.update(usuariorol), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
