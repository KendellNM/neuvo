package com.farm.dolores.farmacia.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "Productos")
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProductos;
    @Column(name = "codigoBarras")
    private String codigoBarras;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "principioActivo")
    private String principioActivo;

    @Column(name = "concentracion")
    private String concentracion;

    @Column(name = "requerireReceta")
    private Boolean requerireReceta;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "precioOferta")
    private Double precioOferta;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "stockMin")
    private Integer stockMin;

    @Column(name = "fecha_vencimiento")
    private Date fecha_vencimiento;

    @Column(name = "imagenUrl")
    private String imagenUrl;

    @Column(name = "qrImageUrl")
    private String qrImageUrl;
    
    @Column(name = "barcodeImageUrl")
    private String barcodeImageUrl;

    @Column(name = "indicaciones")
    private String indicaciones;

    @Column(name = "contraindicaciones")
    private String contraindicaciones;

    @Column(name = "efectosSecundarios")
    private String efectosSecundarios;

    @Column(name = "dosidificador")
    private String dosidificador;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_registro")
    private Date fecha_registro;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    @ManyToOne
    @JoinColumn(name = "laboratorios_id")
    private Laboratorios laboratorios;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "productos")
    @JsonIgnore
    private Set<RecetaDetalle> recetadetalles = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "productos")
    @JsonIgnore
    private Set<PedidoDetalle> pedidodetalles = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "productos")
    @JsonIgnore
    private Set<MovimientosStock> movimientosstocks = new HashSet<>();
}
