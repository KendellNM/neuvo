package com.example.doloresapp.presentation.viewmodel

data class AuthRequest(
    val correo: String,
    val password: String
)

data class AuthResponse(
    val token: String
)