package com.farm.dolores.farmacia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "RecetasDigitales")
public class RecetaDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecetaDigital;

    @Column(name = "imagenUrl", nullable = false)
    private String imagenUrl;

    @Column(name = "textoExtraido", columnDefinition = "TEXT")
    private String textoExtraido;

    @Column(name = "estado")
    private String estado; // PENDIENTE, PROCESADA, VALIDADA, RECHAZADA

    @Column(name = "fechaCreacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "fechaProcesamiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaProcesamiento;

    // Ubicación de entrega del cliente
    @Column(name = "direccionEntrega")
    private String direccionEntrega;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "telefonoContacto")
    private String telefonoContacto;

    @Column(name = "observacionesCliente", columnDefinition = "TEXT")
    private String observacionesCliente;

    // Referencia al pedido creado por el farmacéutico
    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Clientes cliente;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medicos medico;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recetaDigital")
    private Set<RecetaDigitalDetalle> detalles = new HashSet<>();
}
