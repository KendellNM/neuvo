package com.example.doloresapp.domain.model

data class ItemVenta(
    val productoId: Long,
    val nombre: String,
    val precioUnitario: Double,
    var cantidad: Int = 1,
    val imagenUrl: String? = null,
    val codigoBarras: String? = null
) {
    val subtotal: Double
        get() = precioUnitario * cantidad
}