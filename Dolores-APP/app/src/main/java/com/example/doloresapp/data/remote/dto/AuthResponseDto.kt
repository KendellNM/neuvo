package com.example.doloresapp.data.remote.dto

/**
 * Respuesta del backend al hacer login
 * Incluye token JWT y lista de roles del usuario
 */
data class AuthResponseDto(
    val token: String,
    val roles: List<String>
)
