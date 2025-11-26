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
@Table(name = "ConsultaOnline")
public class ConsultaOnline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsultaOnline;
    @Column(name = "tipoConsulta")
    private String tipoConsulta;

    @Column(name = "consulta")
    private String consulta;

    @Column(name = "respuesta")
    private String respuesta;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_consulta")
    private Date fecha_consulta;

    @Column(name = "fecha_respuesta")
    private Date fecha_respuesta;

    @Column(name = "calificacion")
    private Double calificacion;

    @ManyToOne
    @JoinColumn(name = "clientes_id")
    private Clientes clientes;
    @ManyToOne
    @JoinColumn(name = "farmaceuticos_id")
    private Farmaceuticos farmaceuticos;
}
