package com.example.doloresapp.data.remote

import retrofit2.http.*

data class RepartidorResponse(
    val idRepartidores: Long,
    val nombres: String?,
    val apellidos: String?,
    val dni: String?,
    val telefono: String?,
    val vehiculo: String?,
    val placaVehiculo: String?,
    val estado: String?,
    val Repartidores: RepartidorUsuarioRef? = null // Referencia al usuario
)

data class CrearRepartidorRequest(
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String,
    val vehiculo: String? = null,
    val placaVehiculo: String? = null,
    val estado: String = "ACTIVO",
    val Repartidores: RepartidorUsuarioRef? = null // Para asociar con usuario
)

data class RepartidorUsuarioRef(val idUsuarios: Long)

interface RepartidoresApi {
    
    @GET("api/repartidores")
    suspend fun getAllRepartidores(): List<RepartidorResponse>
    
    @GET("api/repartidores/{id}")
    suspend fun getRepartidorById(@Path("id") id: Long): RepartidorResponse
    
    @POST("api/repartidores")
    suspend fun crearRepartidor(@Body repartidor: CrearRepartidorRequest): RepartidorResponse
    
    @DELETE("api/repartidores/{id}")
    suspend fun eliminarRepartidor(@Path("id") id: Long)
}
