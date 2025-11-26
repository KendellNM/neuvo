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
@Table(name = "RecetaDetalle")
public class RecetaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecetaDetalle;
    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "duracion")
    private Date duracion;

    @Column(name = "indicacionesEspeciales")
    private String indicacionesEspeciales;

    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "recetas_id")
    private Recetas recetas;
    @ManyToOne
    @JoinColumn(name = "productos_id")
    private Productos productos;
}
