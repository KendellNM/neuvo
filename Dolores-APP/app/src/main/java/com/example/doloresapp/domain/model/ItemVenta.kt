package com.example.doloresapp.domain.model

data class ItemVenta(
    val productoId: Long,
    val nombre: String,
    val precioUnitario: Double,
    val cantidad: Int = 1,
    val stockDisponible: Int = 0,
    val imagenUrl: String? = null,
    val codigoBarras: String? = null
) {
    val subtotal: Double
        get() = precioUnitario * cantidad
    
    fun puedeIncrementar(): Boolean = cantidad < stockDisponible
    
    fun incrementar(): ItemVenta = if (puedeIncrementar()) copy(cantidad = cantidad + 1) else this
    
    fun decrementar(): ItemVenta = if (cantidad > 1) copy(cantidad = cantidad - 1) else this
}