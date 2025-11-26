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
@Table(name = "NotificacionesPush")
public class NotificacionPush {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Clientes cliente;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "tipo")
    private String tipo; // PEDIDO, PROMOCION, RECORDATORIO, SISTEMA

    @Column(name = "leida")
    private Boolean leida;

    @Column(name = "fechaEnvio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEnvio;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data; // JSON con datos adicionales
}
