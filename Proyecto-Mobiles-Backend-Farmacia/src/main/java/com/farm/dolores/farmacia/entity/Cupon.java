package com.farm.dolores.farmacia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Cupones")
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCupon;

    @Column(name = "codigo", unique = true)
    private String codigo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "puntosRequeridos")
    private Integer puntosRequeridos;

    @Column(name = "descuentoPorcentaje")
    private Double descuentoPorcentaje;

    @Column(name = "descuentoMonto")
    private Double descuentoMonto;

    @Column(name = "fechaInicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "fechaExpiracion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaExpiracion;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "usoMaximo")
    private Integer usoMaximo;

    @Column(name = "usoActual")
    private Integer usoActual;
}
