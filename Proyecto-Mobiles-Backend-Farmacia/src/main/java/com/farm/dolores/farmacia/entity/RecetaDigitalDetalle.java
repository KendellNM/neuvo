package com.farm.dolores.farmacia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "RecetasDigitalesDetalles")
public class RecetaDigitalDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecetaDigitalDetalle;

    @Column(name = "medicamentoTexto")
    private String medicamentoTexto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "dosificacion")
    private String dosificacion;

    @Column(name = "validado")
    private Boolean validado;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "receta_digital_id")
    private RecetaDigital recetaDigital;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Productos producto;
}
