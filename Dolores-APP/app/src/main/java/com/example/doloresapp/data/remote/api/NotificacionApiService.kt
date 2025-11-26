package com.example.doloresapp.data.remote.api

import com.example.doloresapp.data.remote.dto.ApiResponse
import com.example.doloresapp.data.remote.dto.RegistrarDispositivoRequest
import com.example.doloresapp.domain.model.Notificacion
import retrofit2.Response
import retrofit2.http.*

interface NotificacionApiService {
    @POST("api/notificaciones/registrar-dispositivo")
    suspend fun registrarDispositivo(@Body request: RegistrarDispositivoRequest): Response<ApiResponse<Any>>
    
    @GET("api/notificaciones/cliente/{clienteId}")
    suspend fun getNotificaciones(@Path("clienteId") clienteId: Long): Response<List<Notificacion>>
    
    @PUT("api/notificaciones/{id}/marcar-leida")
    suspend fun marcarComoLeida(@Path("id") notificacionId: Long): Response<ApiResponse<Any>>
}
