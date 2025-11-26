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
@Table(name = "MovimientosStock")
public class MovimientosStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimientosStock;
    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "stockAnterior")
    private String stockAnterior;

    @Column(name = "stockActual")
    private String stockActual;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "fechaMovimiento")
    private Date fechaMovimiento;

    @Column(name = "tipoMovimiento")
    private String tipoMovimiento;

    @ManyToOne
    @JoinColumn(name = "usuarios_id")
    private Usuarios usuarios;
    @ManyToOne
    @JoinColumn(name = "productos_id")
    private Productos productos;
    @ManyToOne
    @JoinColumn(name = "pedidos_id")
    private Pedidos pedidos;
}
