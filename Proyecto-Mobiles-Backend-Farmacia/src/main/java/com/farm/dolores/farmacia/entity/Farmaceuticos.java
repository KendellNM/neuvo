package com.farm.dolores.farmacia.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "Farmaceuticos")
public class Farmaceuticos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFarmaceuticos;
    @Column(name = "nombres")
    private String nombres;

    @Column(name = "Apellidos")
    private String Apellidos;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "estado")
    private String estado;

    @Column(name = "calificacion")
    private String calificacion;

    @Column(name = "cqf")
    private String cqf;

    @Column(name = "horario")
    private Date horario;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuarios Farmaceuticos;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "farmaceuticos")
    @JsonIgnore
    private Set<ConsultaOnline> consultaonlines = new HashSet<>();
}
