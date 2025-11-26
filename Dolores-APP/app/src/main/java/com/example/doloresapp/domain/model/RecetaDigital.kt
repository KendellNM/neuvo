package com.example.doloresapp.domain.model

data class RecetaDigital(
    val id: Long,
    val clienteId: Long,
    val imagenUrl: String,
    val textoExtraido: String?,
    val estado: String,
    val fechaProcesamiento: String,
    val detalles: List<RecetaDetalle>?
)

data class RecetaDetalle(
    val id: Long,
    val medicamentoTexto: String,
    val productoId: Long?,
    val productoNombre: String?,
    val validado: Boolean
)
