package com.example.doloresapp.data.remote

import retrofit2.http.*

data class DireccionResponse(
    val idDirecciones: Long,
    val nombre: String?,
    val direccion: String?,
    val distrito: String?,
    val provincia: String?,
    val departamento: String?,
    val referencia: String?,
    val latitud: String?,
    val longitud: String?,
    val estado: String?
)

data class CrearDireccionRequest(
    val nombre: String,
    val direccion: String,
    val distrito: String?,
    val provincia: String?,
    val departamento: String?,
    val referencia: String?,
    val latitud: Double?,
    val longitud: Double?,
    val clienteId: Long
)

interface DireccionesApi {
    
    @GET("api/direcciones/cliente/{clienteId}")
    suspend fun getDireccionesByCliente(@Path("clienteId") clienteId: Long): List<DireccionResponse>
    
    @POST("api/direcciones")
    suspend fun crearDireccion(@Body request: CrearDireccionRequest): DireccionResponse
    
    @DELETE("api/direcciones/{id}")
    suspend fun eliminarDireccion(@Path("id") id: Long)
}
