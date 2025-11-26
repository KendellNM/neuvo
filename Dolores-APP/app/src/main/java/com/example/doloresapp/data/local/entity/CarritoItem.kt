package com.example.doloresapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class CarritoItem(
    @PrimaryKey
    val productoId: Long,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val imagenUrl: String? = null
) {
    val subtotal: Double get() = precio * cantidad
}
