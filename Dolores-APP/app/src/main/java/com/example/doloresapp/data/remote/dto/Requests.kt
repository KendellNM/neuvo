package com.example.doloresapp.data.remote.dto

data class CanjearPuntosRequest(
    val clienteId: Long,
    val puntosACanjear: Int,
    val descripcionCupon: String
)

data class RegistrarDispositivoRequest(
    val clienteId: Long,
    val fcmToken: String,
    val plataforma: String = "ANDROID"
)

data class UbicacionDeliveryRequest(
    val pedidoId: Long,
    val latitud: Double,
    val longitud: Double
)
