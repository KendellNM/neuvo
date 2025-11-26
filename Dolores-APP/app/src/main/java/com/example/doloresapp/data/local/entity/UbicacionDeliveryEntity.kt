package com.example.doloresapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ubicaciones_delivery")
data class UbicacionDeliveryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,
    val latitud: Double,
    val longitud: Double,
    val timestamp: Date
)
