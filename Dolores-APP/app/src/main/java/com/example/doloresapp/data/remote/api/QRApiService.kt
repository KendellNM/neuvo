package com.example.doloresapp.data.remote.api

import com.example.doloresapp.domain.model.Producto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QRApiService {
    @GET("api/productos/{id}/mobile")
    suspend fun getProductoByQR(@Path("id") productoId: Long): Response<Producto>
}
