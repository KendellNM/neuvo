package com.example.doloresapp.data.remote

import retrofit2.http.*

data class PedidoResponse(
    val idPedidos: Long,
    val numeroPedido: Int?,
    val subtotal: Double?,
    val descuento: Double?,
    val costoDelivery: Double?,
    val total: Double?,
    val metodoPago: String?,
    val estado: String?,
    val fechaPedido: String?,
    val fechaEntregaEstimada: String?,
    val fechaEntregaReal: String?,
    val observaciones: String?,
    val problemas: String?,
    val clientes: ClienteInfo?,
    val direcciones: DireccionInfo?,
    val repartidor: RepartidorInfo?
)

data class ClienteInfo(
    val idClientes: Long?,
    val nombres: String?,
    val apellidos: String?,
    val telefono: String?
)

data class DireccionInfo(
    val idDirecciones: Long?,
    val direccion: String?,
    val referencia: String?,
    val latitud: String?,  // Backend devuelve String
    val longitud: String?, // Backend devuelve String
    val distrito: String?,
    val provincia: String?
) {
    // Helpers para obtener coordenadas como Double
    fun getLatitudDouble(): Double? = latitud?.toDoubleOrNull()
    fun getLongitudDouble(): Double? = longitud?.toDoubleOrNull()
}

data class RepartidorInfo(
    val idRepartidores: Long?,
    val nombres: String?,
    val apellidos: String?
)

interface PedidosApi {
    
    @GET("api/pedidos")
    suspend fun getAllPedidos(): List<PedidoResponse>
    
    @GET("api/pedidos/{id}")
    suspend fun getPedidoById(@Path("id") id: Long): PedidoResponse
    
    @GET("api/pedidos/repartidor/{repartidorId}")
    suspend fun getPedidosByRepartidor(@Path("repartidorId") repartidorId: Long): List<PedidoResponse>
    
    @GET("api/pedidos/listos-para-asignar")
    suspend fun getPedidosListosParaAsignar(): List<PedidoResponse>
    
    @GET("api/pedidos/estado/{estado}")
    suspend fun getPedidosByEstado(@Path("estado") estado: String): List<PedidoResponse>
    
    @PUT("api/pedidos/{pedidoId}/asignar/{repartidorId}")
    suspend fun asignarPedido(
        @Path("pedidoId") pedidoId: Long,
        @Path("repartidorId") repartidorId: Long
    ): PedidoResponse
    
    @PUT("api/pedidos/{pedidoId}/estado")
    suspend fun cambiarEstado(
        @Path("pedidoId") pedidoId: Long,
        @Query("nuevoEstado") nuevoEstado: String
    ): PedidoResponse
}
