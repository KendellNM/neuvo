package com.farm.dolores.farmacia.entity;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "Laboratorios")
public class Laboratorios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLaboratorios;
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "correo")
    private String correo;

    @Column(name = "pais_orien")
    private String pais_orien;

    @Column(name = "estado")
    private String estado;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "laboratorios")
    @JsonIgnore
    private Set<Productos> productoss = new HashSet<>();
}
