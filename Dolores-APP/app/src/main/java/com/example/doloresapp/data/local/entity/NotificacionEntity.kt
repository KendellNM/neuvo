package com.example.doloresapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notificaciones")
data class NotificacionEntity(
    @PrimaryKey val id: Long,
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val leida: Boolean,
    val fecha: Date,
    val datos: String?
)
