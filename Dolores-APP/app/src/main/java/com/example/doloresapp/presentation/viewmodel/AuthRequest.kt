package com.example.doloresapp.presentation.viewmodel

data class AuthRequest(
    val username: String,  // Puede ser DNI (clientes) o correo (admin/staff)
    val password: String
)

data class AuthResponse(
    val token: String,
    val roles: List<String>? = null
)