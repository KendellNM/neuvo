package com.example.doloresapp.data.remote.service

import com.example.doloresapp.data.remote.dto.CategoriaDTO
import com.example.doloresapp.data.remote.dto.ProductoDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductoApiService {
    @GET("api/productos")
    suspend fun getProductos(): List<ProductoDTO>

    @GET("api/categorias")
    suspend fun getCategorias(): List<CategoriaDTO>

    @GET("api/productos/{id}")
    suspend fun getProducto(@Path("id") id: Int): ProductoDTO
}
