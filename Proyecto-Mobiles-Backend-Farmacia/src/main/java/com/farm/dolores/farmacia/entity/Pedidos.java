package com.farm.dolores.farmacia.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "Pedidos")
public class Pedidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedidos;
    @Column(name = "numeroPedido")
    private Integer numeroPedido;

    @Column(name = "subtotal")
    private Double subtotal;

    @Column(name = "descuento")
    private Double descuento;

    @Column(name = "costoDelivery")
    private Double costoDelivery;

    @Column(name = "total")
    private Double total;

    @Column(name = "metodoPago")
    private String metodoPago;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fechaPedido")
    private Date fechaPedido;

    @Column(name = "fechaEntregaEstimada")
    private Date fechaEntregaEstimada;

    @Column(name = "fechaEntregaReal")
    private Date fechaEntregaReal;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "problemas")
    private String problemas;
    
    @Column(name = "tipoVenta")
    private String tipoVenta; // DELIVERY, PRESENCIAL

    @ManyToOne
    @JoinColumn(name = "clientes_id")
    private Clientes clientes;
    
    @ManyToOne
    @JoinColumn(name = "direcciones_id")
    private Direcciones direcciones;
    
    @ManyToOne
    @JoinColumn(name = "recetas_id")
    private Recetas recetas;
    
    @ManyToOne
    @JoinColumn(name = "repartidor_id")
    private Repartidores repartidor;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pedidos")
    @JsonIgnore
    private Set<Entregas> entregass = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pedidos")
    @JsonIgnore
    private Set<MovimientosStock> movimientosstocks = new HashSet<>();
}
