package com.example.doloresapp.data.remote.api

import com.example.doloresapp.data.remote.dto.ProductoDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QRApiService {
    @GET("api/productos/{id}")
    suspend fun getProductoByQR(@Path("id") productoId: Long): Response<ProductoDTO>
    
    @GET("api/productos/codigo/{codigoBarras}")
    suspend fun getProductoByCodigoBarras(@Path("codigoBarras") codigoBarras: String): Response<ProductoDTO>
}