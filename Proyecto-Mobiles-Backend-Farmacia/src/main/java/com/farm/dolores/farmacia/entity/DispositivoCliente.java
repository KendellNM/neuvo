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
@Table(name = "DispositivosClientes")
public class DispositivoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDispositivo;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Clientes cliente;

    @Column(name = "fcmToken", unique = true)
    private String fcmToken;

    @Column(name = "plataforma")
    private String plataforma;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "fechaRegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "fechaUltimaActividad")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUltimaActividad;
}
