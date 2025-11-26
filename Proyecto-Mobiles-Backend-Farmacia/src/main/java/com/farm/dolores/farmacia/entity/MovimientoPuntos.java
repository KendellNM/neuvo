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
@Table(name = "MovimientosPuntos")
public class MovimientoPuntos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimientoPuntos;

    @ManyToOne
    @JoinColumn(name = "programa_fidelizacion_id")
    private ProgramaFidelizacion programaFidelizacion;

    @Column(name = "puntos")
    private Integer puntos;

    @Column(name = "tipo")
    private String tipo; // GANADO, CANJEADO, EXPIRADO

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;
}
