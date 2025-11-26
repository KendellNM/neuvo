package com.example.doloresapp.domain.model

import com.example.doloresapp.data.remote.dto.CategoriaDTO
import com.example.doloresapp.data.remote.dto.ProductoDTO
import java.io.Serializable

data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val concentracion: String?,
    val precioOferta: Double?,
    val imagenUrl: String?,
    val stock: Int,
    val categoriaId: Long?
) : Serializable

data class Categoria(
    val id: Long,
    val nombre: String
)

fun ProductoDTO.toDomain() = Producto(
    id = this.id,
    nombre = this.nombre,
    descripcion = this.descripcion,
    precio = this.precio,
    concentracion = this.concentracion,
    precioOferta = this.precioOferta,
    imagenUrl = this.imagen_url,
    stock = this.stock,
    categoriaId = this.categoria?.id
)

fun CategoriaDTO.toDomain() = Categoria(
    id = this.id,
    nombre = this.nombre
)
