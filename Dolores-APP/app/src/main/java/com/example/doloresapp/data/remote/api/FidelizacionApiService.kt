package com.example.doloresapp.data.remote.api

import com.example.doloresapp.data.remote.dto.ApiResponse
import com.example.doloresapp.data.remote.dto.CanjearPuntosRequest
import com.example.doloresapp.domain.model.MovimientoPuntos
import com.example.doloresapp.domain.model.ProgramaFidelizacion
import retrofit2.Response
import retrofit2.http.*

interface FidelizacionApiService {
    @POST("api/fidelizacion/crear")
    suspend fun crearPrograma(@Query("clienteId") clienteId: Long): Response<ProgramaFidelizacion>
    
    @GET("api/fidelizacion/cliente/{clienteId}")
    suspend fun getPuntos(@Path("clienteId") clienteId: Long): Response<ProgramaFidelizacion>
    
    @POST("api/fidelizacion/canjear")
    suspend fun canjearPuntos(@Body request: CanjearPuntosRequest): Response<ApiResponse<Any>>
    
    @GET("api/fidelizacion/historial/{clienteId}")
    suspend fun getHistorial(@Path("clienteId") clienteId: Long): Response<List<MovimientoPuntos>>
}
