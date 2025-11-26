package com.farm.dolores.farmacia.security;

import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.Roles;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.repository.ClientesRepository;
import com.farm.dolores.farmacia.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find by DNI first (for clients)
        Clientes cliente = clientesRepository.findByDni(username).orElse(null);
        
        if (cliente != null && cliente.getClientes() != null) {
            Usuarios u = cliente.getClientes();
            Set<GrantedAuthority> authorities = u.getUsuariorols().stream()
                    .map(UsuarioRol::getRoles)
                    .map(Roles::getNombre)
                    .map(name -> name != null && name.startsWith("ROLE_") ? name : "ROLE_" + name)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            boolean enabled = u.getEstado() == null || !"INACTIVO".equalsIgnoreCase(u.getEstado());
            // Use DNI as the Spring Security username (principal)
            return User.withUsername(cliente.getDni())
                    .password(u.getContrasena())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(!enabled)
                    .build();
        }
        
        // Fallback to email for non-client users (admin, staff, etc.)
        Usuarios u = usuariosRepository.findByCorreo(username).orElse(null);
        
        if (u == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        Set<GrantedAuthority> authorities = u.getUsuariorols().stream()
                .map(UsuarioRol::getRoles)
                .map(Roles::getNombre)
                .map(name -> name != null && name.startsWith("ROLE_") ? name : "ROLE_" + name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        boolean enabled = u.getEstado() == null || !"INACTIVO".equalsIgnoreCase(u.getEstado());
        // Use email as the Spring Security username (principal)
        return User.withUsername(u.getCorreo())
                .password(u.getContrasena())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!enabled)
                .build();
    }
}


