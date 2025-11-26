package com.example.doloresapp.domain.model

data class Notificacion(
    val id: Long,
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val leida: Boolean,
    val fecha: String,
    val datos: Map<String, Any>?
)
