package com.example.doloresapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PedidoDTO(
    val id: Long,
    val clienteId: Long?,
    val estado: String,
    val total: Double,
    val direccionEntrega: String?,
    val telefono: String?,
    val notas: String?,
    @SerializedName("fechaCreacion")
    val fechaCreacion: String?,
    val detalles: List<PedidoDetalleDTO>?
)

data class PedidoDetalleDTO(
    val id: Long,
    val productoId: Long,
    val productoNombre: String?,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

data class CrearPedidoRequest(
    val clienteId: Long,
    val direccionEntrega: String,
    val telefono: String,
    val notas: String?,
    val detalles: List<PedidoDetalleRequest>
)

data class PedidoDetalleRequest(
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double
)
