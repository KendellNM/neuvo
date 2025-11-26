package com.example.doloresapp.data.remote.api

import com.example.doloresapp.data.remote.dto.ApiResponse
import com.example.doloresapp.domain.model.RecetaDigital
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RecetaDigitalApiService {
    @Multipart
    @POST("api/recetas-digitales/procesar")
    suspend fun procesarReceta(
        @Part imagen: MultipartBody.Part,
        @Part("clienteId") clienteId: RequestBody
    ): Response<ApiResponse<RecetaDigital>>
    
    @GET("api/recetas-digitales/cliente/{clienteId}")
    suspend fun getRecetasByCliente(@Path("clienteId") clienteId: Long): Response<List<RecetaDigital>>
    
    @GET("api/recetas-digitales/{id}")
    suspend fun getRecetaById(@Path("id") recetaId: Long): Response<RecetaDigital>
    
    @PUT("api/recetas-digitales/{id}/validar")
    suspend fun validarReceta(
        @Path("id") recetaId: Long,
        @Body validacion: Map<String, Boolean>
    ): Response<ApiResponse<RecetaDigital>>
}
