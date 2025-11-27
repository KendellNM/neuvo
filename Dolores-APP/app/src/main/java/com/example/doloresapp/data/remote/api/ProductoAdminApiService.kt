package com.example.doloresapp.data.remote.api

import com.example.doloresapp.data.remote.dto.ProductoDTO
import retrofit2.Response
import retrofit2.http.*

interface ProductoAdminApiService {
    
    @GET("api/productos")
    suspend fun getAllProductos(): Response<List<ProductoDTO>>
    
    @GET("api/productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): Response<ProductoDTO>
    
    @POST("api/productos")
    suspend fun crearProducto(@Body producto: CrearProductoRequest): Response<ProductoDTO>
    
    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Long, @Body producto: ActualizarProductoRequest): Response<ProductoDTO>
    
    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>
    
    @GET("api/productos/{id}/qr")
    suspend fun getQR(@Path("id") id: Long): Response<Map<String, String>>
    
    @POST("api/productos/{id}/qr/regenerar")
    suspend fun regenerarQR(@Path("id") id: Long): Response<Map<String, String>>
    
    @POST("api/productos/qr/generar-todos")
    suspend fun generarTodosQR(): Response<Map<String, Any>>
}

data class CrearProductoRequest(
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int,
    val codigoBarras: String? = null,
    val principioActivo: String? = null,
    val concentracion: String? = null,
    val requerireReceta: Boolean = false
)

data class ActualizarProductoRequest(
    val idProductos: Long,
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int,
    val codigoBarras: String? = null,
    val principioActivo: String? = null,
    val concentracion: String? = null,
    val requerireReceta: Boolean = false
)