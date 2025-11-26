package com.example.doloresapp.presentation.viewmodel

/**
 * Respuesta del backend al hacer registro de cliente
 */
data class AuthResponse(
    val usuarioId: Long? = null,
    val clienteId: Long? = null,
    val usuario: String? = null,
    val correo: String? = null,
    val nombres: String? = null,
    val apellidos: String? = null,
    val dni: String? = null,
    val telefono: String? = null,
    val token: String,
    val roles: List<String> = emptyList()
)
