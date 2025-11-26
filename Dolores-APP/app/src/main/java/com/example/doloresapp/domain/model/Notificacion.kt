package com.example.doloresapp.domain.model

import com.google.gson.annotations.SerializedName

data class Notificacion(
    val id: Long,
    val titulo: String?,
    val mensaje: String?,
    val tipo: String?,
    val leida: Boolean = false,
    @SerializedName("fechaEnvio")
    val fecha: String?,
    val pedidoId: Long? = null,
    @SerializedName("data")
    val datos: String? = null
)
