package com.example.doloresapp.domain.model

data class ProgramaFidelizacion(
    val id: Long,
    val clienteId: Long,
    val puntosActuales: Int,
    val puntosAcumulados: Int,
    val nivelMembresia: String,
    val siguienteNivel: String?,
    val puntosParaSiguienteNivel: Int?
)

data class MovimientoPuntos(
    val id: Long,
    val tipo: String,
    val puntos: Int,
    val descripcion: String,
    val fecha: String
)
