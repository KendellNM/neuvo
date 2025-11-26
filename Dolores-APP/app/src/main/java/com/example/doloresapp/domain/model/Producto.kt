package com.example.doloresapp.domain.model

import com.example.doloresapp.data.remote.dto.CategoriaDTO
import com.example.doloresapp.data.remote.dto.ProductoDTO
import java.io.Serializable

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val concentracion: String,
    val precioOferta: Double?,
    val imagenUrl: String?,
    val stock: Int,
    val categoriaId: Int
) : Serializable

data class Categoria(
    val id: Int,
    val nombre: String
)

fun ProductoDTO.toDomain() = Producto(
    id = this.id,
    nombre = this.nombre,
    descripcion = this.descripcion,
    precio = this.precio,
    concentracion = this.concentracion,
    precioOferta = this.precioOferta,
    imagenUrl = this.imagenUrl,
    stock = this.stock,
    categoriaId = this.categoriaId
)

fun CategoriaDTO.toDomain() = Categoria(
    id = this.id,
    nombre = this.nombre
)
