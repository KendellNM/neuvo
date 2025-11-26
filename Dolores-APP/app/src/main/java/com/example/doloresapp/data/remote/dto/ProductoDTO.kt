package com.example.doloresapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductoDTO(
    @SerializedName("idProductos")
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val concentracion: String?,
    val precioOferta: Double?,
    @SerializedName("imagenUrl")
    val imagen_url: String?,
    val stock: Int,
    val principioActivo: String?,
    val requerireReceta: Boolean?,
    val categoria: CategoriaDTO?
)