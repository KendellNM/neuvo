package com.example.doloresapp.data.remote.service

import com.example.doloresapp.data.remote.dto.CategoriaDTO
import com.example.doloresapp.data.remote.dto.ProductoDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductoApiService {
    @GET("api/productos")
    suspend fun getAllProductos(): List<ProductoDTO>

    @GET("api/categorias")
    suspend fun getCategorias(): List<CategoriaDTO>

    @GET("api/productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): ProductoDTO
    
    @GET("api/productos/categoria/{categoriaId}")
    suspend fun getProductosByCategoria(@Path("categoriaId") categoriaId: Long): List<ProductoDTO>
    
    @GET("api/productos/buscar")
    suspend fun buscarProductos(@Query("nombre") nombre: String): List<ProductoDTO>
}
