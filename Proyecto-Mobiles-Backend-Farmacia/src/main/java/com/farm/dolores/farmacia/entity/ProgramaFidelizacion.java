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
@Table(name = "ProgramaFidelizacion")
public class ProgramaFidelizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProgramaFidelizacion;

    @OneToOne
    @JoinColumn(name = "cliente_id", unique = true)
    private Clientes cliente;

    @Column(name = "puntosActuales")
    private Integer puntosActuales;

    @Column(name = "puntosAcumulados")
    private Integer puntosAcumulados;

    @Column(name = "nivelMembresia")
    private String nivelMembresia; // BRONCE, PLATA, ORO, PLATINO

    @Column(name = "fechaRegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "fechaUltimaActualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUltimaActualizacion;
}
