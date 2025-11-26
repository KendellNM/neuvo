package com.example.doloresapp.presentation.viewmodel

import com.google.gson.annotations.SerializedName

data class UsuarioResponse(
    @SerializedName("id") val id: Long? = null,
    // Algunos backends pueden devolver "usuario" o "username"
    @SerializedName("usuario") val usuario: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("estado") val estado: String? = null,
    // Campos alternativos por si el nombre mostrable viene separado
    @SerializedName("nombres") val nombres: String? = null,
    @SerializedName("apellidos") val apellidos: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("dni") val dni: String? = null,
    // IDs específicos por rol
    @SerializedName("repartidorId") val repartidorId: Long? = null,
    @SerializedName("clienteId") val clienteId: Long? = null,
)
