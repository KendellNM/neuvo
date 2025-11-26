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
    val estado: String?
)

interface RepartidoresApi {
    
    @GET("api/repartidores")
    suspend fun getAllRepartidores(): List<RepartidorResponse>
    
    @GET("api/repartidores/{id}")
    suspend fun getRepartidorById(@Path("id") id: Long): RepartidorResponse
}
