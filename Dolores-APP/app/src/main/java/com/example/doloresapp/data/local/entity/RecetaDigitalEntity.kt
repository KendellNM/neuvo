package com.example.doloresapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "recetas_digitales")
data class RecetaDigitalEntity(
    @PrimaryKey val id: Long,
    val clienteId: Long,
    val imagenUrl: String,
    val textoExtraido: String?,
    val estado: String,
    val fechaProcesamiento: Date
)
