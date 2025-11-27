package com.example.doloresapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entidad para almacenar categorías en cache local
 */
@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey
    val id: Long,
    val nombre: String,
    val lastUpdated: Date = Date()
)
