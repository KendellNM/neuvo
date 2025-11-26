package com.example.doloresapp.data.remote.api

import com.example.doloresapp.data.remote.dto.CrearPedidoPresencialRequest
import com.example.doloresapp.data.remote.dto.CrearPedidoRequest
import com.example.doloresapp.data.remote.dto.PedidoDTO
import retrofit2.Response
import retrofit2.http.*

interface PedidoApiService {
    
    @GET("api/pedidos/cliente/{clienteId}")
    suspend fun getPedidosByCliente(@Path("clienteId") clienteId: Long): List<PedidoDTO>
    
    @GET("api/pedidos/{id}")
    suspend fun getPedidoById(@Path("id") id: Long): PedidoDTO
    
    @POST("api/pedidos/mobile")
    suspend fun crearPedido(@Body request: CrearPedidoRequest): PedidoDTO
    
    @PUT("api/pedidos/{id}/estado")
    suspend fun actualizarEstado(
        @Path("id") id: Long,
        @Query("nuevoEstado") estado: String
    ): PedidoDTO
    
    // Venta presencial (sin delivery) - para farmacéutico
    @POST("api/pedidos/presencial")
    suspend fun crearPedidoPresencial(@Body request: CrearPedidoPresencialRequest): Response<PedidoDTO>
}
