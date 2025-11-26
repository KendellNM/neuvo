package com.farm.dolores.farmacia.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
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
@Table(name = "Entregas")
public class Entregas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEntregas;
    @Column(name = "fecha_asignacion")
    private Date fecha_asignacion;

    @Column(name = "fecha_recojo")
    private Date fecha_recojo;

    @Column(name = "fecha_entrega")
    private Date fecha_entrega;

    @Column(name = "estado")
    private String estado;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "foto_entrega")
    private String foto_entrega;

    @Column(name = "firma_digital")
    private String firma_digital;

    @ManyToOne
    @JoinColumn(name = "pedidos_id")
    private Pedidos pedidos;
    @ManyToOne
    @JoinColumn(name = "repartidor_id")
    private Repartidores repartidores;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "entregas")
    @JsonIgnore
    private Set<SeguimientoEntrega> seguimientoentregas = new HashSet<>();
}
