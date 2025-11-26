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
@Table(name = "Recetas")
public class Recetas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecetas;
    @Column(name = "fecha_emision")
    private Date fecha_emision;

    @Column(name = "fecha_vencimiento")
    private Date fecha_vencimiento;

    @Column(name = "diagnostico")
    private String diagnostico;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "estado")
    private String estado;

    @Column(name = "imagen_receta_url")
    private String imagen_receta_url;

    @Column(name = "esValidado")
    private Boolean esValidado;

    @Column(name = "fecha_validacion")
    private Date fecha_validacion;

    @Column(name = "motivo_rechazo")
    private String motivo_rechazo;

    @Column(name = "numero_receta")
    private String numero_receta;

    @ManyToOne
    @JoinColumn(name = "usuarios_id")
    private Usuarios usuarios;
    @ManyToOne
    @JoinColumn(name = "clientes_id")
    private Clientes clientes;
    @ManyToOne
    @JoinColumn(name = "medicos_id")
    private Medicos medicos;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recetas")
    @JsonIgnore
    private Set<Pedidos> pedidoss = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recetas")
    @JsonIgnore
    private Set<RecetaDetalle> recetadetalles = new HashSet<>();
}
