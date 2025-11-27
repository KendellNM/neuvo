package com.example.doloresapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entidad para almacenar pedidos creados offline que esperan sincronización
 */
@Entity(tableName = "pedidos_pendientes")
data class PedidoPendienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clienteId: Long,
    val direccionId: Long?,
    val direccionEntrega: String,
    val telefono: String?,
    val notas: String?,
    val metodoPago: String,
    val latitud: Double?,
    val longitud: Double?,
    val detallesJson: String, // JSON con los detalles del pedido
    val total: Double,
    val fechaCreacion: Date = Date(),
    val sincronizado: Boolean = false,
    val intentosSincronizacion: Int = 0,
    val ultimoError: String? = null
)
