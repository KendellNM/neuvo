package com.farm.dolores.farmacia.entity;

import java.util.Date;
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
@Table(name = "Notificaciones")
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificaciones;
    @Column(name = "titulo")
    private String titulo;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_creacion")
    private Date fecha_creacion;

    @Column(name = "tipo")
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "usuarios_id")
    private Usuarios usuarios;
}
