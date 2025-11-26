package com.example.doloresapp.presentation.viewmodel

data class RegisterRequest(
    val usuario: String,
    val correo: String,
    val password: String,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String,
    val genero: String,
    val fechaNacimiento: String
)

// Nota: El género debe ser "M" o "F" según la API
