package com.example.doloresapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey val id: Long,
    val clienteId: Long,
    val estado: String,
    val total: Double,
    val fechaCreacion: Date,
    val direccionEntrega: String?,
    val lastUpdated: Date = Date()
)
