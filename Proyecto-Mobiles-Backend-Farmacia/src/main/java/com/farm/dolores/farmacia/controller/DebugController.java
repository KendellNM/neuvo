package com.farm.dolores.farmacia.controller;

import com.farm.dolores.farmacia.entity.Roles;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.repository.RolesRepository;
import com.farm.dolores.farmacia.repository.UsuarioRolRepository;
import com.farm.dolores.farmacia.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final UsuariosRepository usuariosRepository;
    private final RolesRepository rolesRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        List<Usuarios> usuarios = usuariosRepository.findAll();
        List<Roles> roles = rolesRepository.findAll();
        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsuarios", usuarios.size());
        response.put("totalRoles", roles.size());
        response.put("totalAsignaciones", usuarioRoles.size());
        
        response.put("usuarios", usuarios.stream().map(u -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", u.getId());
            userMap.put("usuario", u.getUsuario());
            userMap.put("correo", u.getCorreo());
            userMap.put("estado", u.getEstado());
            
            // Buscar roles del usuario
            List<String> rolesUsuario = usuarioRolRepository.findAll().stream()
                .filter(ur -> ur.getUsuarios().getId().equals(u.getId()))
                .map(ur -> ur.getRoles().getNombre())
                .collect(Collectors.toList());
            userMap.put("roles", rolesUsuario);
            
            return userMap;
        }).collect(Collectors.toList()));
        
        response.put("roles", roles.stream().map(r -> {
            Map<String, Object> rolMap = new HashMap<>();
            rolMap.put("id", r.getId());
            rolMap.put("nombre", r.getNombre());
            rolMap.put("descripcion", r.getDescripcion());
            return rolMap;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<?> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("mensaje", "Endpoint de debug para verificar usuarios");
        info.put("endpoints", new String[]{
            "GET /api/debug/usuarios - Lista todos los usuarios y roles",
            "GET /api/debug/info - Muestra esta información"
        });
        return ResponseEntity.ok(info);
    }
}
