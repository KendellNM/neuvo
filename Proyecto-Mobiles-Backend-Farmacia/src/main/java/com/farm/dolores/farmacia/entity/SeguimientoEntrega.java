package com.farm.dolores.farmacia.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "SeguimientoEntrega")
public class SeguimientoEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSeguimientoEntrega;
    @Column(name = "tiempo")
    private Date tiempo;

    @Column(name = "latitud")
    private String latitud;

    @Column(name = "longitud")
    private String longitud;

    @ManyToOne
    @JoinColumn(name = "entregas_id")
    private Entregas entregas;
}
