package com.example.doloresapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PedidoDTO(
    @SerializedName("idPedidos")
    val id: Long,
    val clienteId: Long?,
    val estado: String?,
    val total: Double?,
    @SerializedName("direcciones")
    val direccionInfo: DireccionDTO?,
    val telefono: String?,
    @SerializedName("observaciones")
    val notas: String?,
    @SerializedName("fechaPedido")
    val fechaCreacion: String?,
    val detalles: List<PedidoDetalleDTO>?
) {
    // Propiedad calculada para obtener la dirección como String
    val direccionEntrega: String?
        get() = direccionInfo?.direccion
}

data class DireccionDTO(
    val idDirecciones: Long?,
    val direccion: String?,
    val referencia: String?,
    val latitud: String?,  // Backend devuelve String
    val longitud: String?, // Backend devuelve String
    val distrito: String?,
    val provincia: String?
) {
    fun getLatitudDouble(): Double? = latitud?.toDoubleOrNull()
    fun getLongitudDouble(): Double? = longitud?.toDoubleOrNull()
}

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
    val direccionId: Long? = null,  // Opcional, si selecciona una dirección existente
    val direccionEntrega: String,   // Texto de la dirección
    val telefono: String? = null,
    val notas: String? = null,
    val metodoPago: String? = "EFECTIVO",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val detalles: List<PedidoDetalleRequest>
)

data class PedidoDetalleRequest(
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double
)
