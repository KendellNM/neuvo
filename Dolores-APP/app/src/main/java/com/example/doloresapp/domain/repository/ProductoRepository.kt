package com.example.doloresapp.domain.repository

import com.example.doloresapp.domain.model.Categoria
import com.example.doloresapp.domain.model.Producto

interface ProductoRepository {
    suspend fun getProductos(): List<Producto>
    suspend fun getCategorias(): List<Categoria>
    suspend fun searchProductos(query: String): List<Producto>
}