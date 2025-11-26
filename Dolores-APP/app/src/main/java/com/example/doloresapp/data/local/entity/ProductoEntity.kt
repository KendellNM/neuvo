package com.example.doloresapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int,
    val disponible: Boolean,
    val imagenUrl: String?,
    val laboratorioNombre: String?,
    val categoria: String?,
    val requiereReceta: Boolean,
    val lastUpdated: Date = Date()
)
