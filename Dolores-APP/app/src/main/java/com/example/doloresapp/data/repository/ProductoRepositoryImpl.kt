package com.example.doloresapp.data.repository

import com.example.doloresapp.data.remote.service.ProductoApiService
import com.example.doloresapp.domain.model.Categoria
import com.example.doloresapp.domain.model.Producto
import com.example.doloresapp.domain.model.toDomain
import com.example.doloresapp.domain.repository.ProductoRepository
import android.util.Log

class ProductoRepositoryImpl(private val apiService: ProductoApiService) : ProductoRepository {
    override suspend fun getProductos(): List<Producto> {
        Log.d("ProductoRepository", "getProductos(): llamando API /productos ...")
        val productosDto = apiService.getAllProductos()
        Log.d("ProductoRepository", "getProductos(): respuesta size DTO = ${productosDto.size}")
        return productosDto.map { it.toDomain() }
    }

    override suspend fun getCategorias(): List<Categoria> {
        Log.d("ProductoRepository", "getCategorias(): llamando API /categorias ...")
        val categoriasDto = apiService.getCategorias()
        Log.d("ProductoRepository", "getCategorias(): respuesta size DTO = ${categoriasDto.size}")
        return categoriasDto.map { it.toDomain() }
    }

    override suspend fun searchProductos(query: String): List<Producto> {
        Log.d("ProductoRepository", "searchProductos(): query='$query'")
        val productosDto = apiService.getAllProductos()
        val productosFiltrados = productosDto.filter { it.nombre.contains(query, ignoreCase = true) }
        Log.d("ProductoRepository", "searchProductos(): respuesta size filtrado = ${productosFiltrados.size}")
        return productosFiltrados.map { it.toDomain() }
    }
}
