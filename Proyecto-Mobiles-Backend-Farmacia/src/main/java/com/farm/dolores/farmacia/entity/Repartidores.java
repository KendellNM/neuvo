package com.farm.dolores.farmacia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@Table(name = "Repartidores")
public class Repartidores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRepartidores;
    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "dni")
    private String dni;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "licenciaConducir")
    private String licenciaConducir;

    @Column(name = "vehiculo")
    private String vehiculo;

    @Column(name = "placaVehiculo")
    private String placaVehiculo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "calificacion")
    private String calificacion;

    @Column(name = "fechaIngreso")
    private Date fechaIngreso;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuarios Repartidores;
    @OneToMany(mappedBy = "repartidores")
    private List<Entregas> entregass;
}
