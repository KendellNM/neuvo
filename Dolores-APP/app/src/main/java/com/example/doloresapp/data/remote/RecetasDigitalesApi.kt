package com.example.doloresapp.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

// ==================== DTOs de Respuesta ====================

data class ApiResponseWrapper<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class RecetaDigitalResponse(
    val id: Long,
    val imagenUrl: String?,
    val textoExtraido: String?,
    val estado: String?,
    val fechaCreacion: String?,
    val fechaProcesamiento: String?,
    val clienteId: Long?,
    val clienteNombre: String?,
    val direccionEntrega: String?,
    val latitud: Double?,
    val longitud: Double?,
    val telefonoContacto: String?,
    val observacionesCliente: String?,
    val pedidoId: Long?,
    val detalles: List<RecetaDetalleResponse>?
)

data class RecetaDetalleResponse(
    val id: Long?,
    val medicamentoTexto: String?,
    val validado: Boolean?,
    val productoId: Long?,
    val productoNombre: String?
)

data class PedidoRecetaResponse(
    val id: Long,
    val numeroPedido: Int?,
    val subtotal: Double?,
    val descuento: Double?,
    val costoDelivery: Double?,
    val total: Double?,
    val metodoPago: String?,
    val estado: String?,
    val tipoVenta: String?,
    val fechaPedido: String?,
    val observaciones: String?,
    val direccionEntrega: String?,
    val latitud: Double?,
    val longitud: Double?,
    val clienteTelefono: String?,
    val recetaDigitalId: Long?,
    val productos: List<ProductoPedidoResponse>?
)

data class ProductoPedidoResponse(
    val productoId: Long?,
    val productoNombre: String?,
    val cantidad: Int?,
    val precioUnitario: Double?,
    val subtotal: Double?
)

// ==================== DTOs de Request ====================

data class ProcesarRecetaRequest(
    val recetaId: Long,
    val productos: List<ProductoPedidoItem>,
    val observacionesFarmaceutico: String?
)

data class ProductoPedidoItem(
    val productoId: Long,
    val cantidad: Int
)

// ==================== API Interface ====================

interface RecetasDigitalesApi {
    
    // ========== ENDPOINTS PARA CLIENTE ==========
    
    /**
     * Cliente envía receta con ubicación de entrega
     */
    @Multipart
    @POST("api/recetas-digitales/enviar-con-ubicacion")
    suspend fun enviarRecetaConUbicacion(
        @Part imagen: MultipartBody.Part,
        @Part("direccionEntrega") direccionEntrega: RequestBody,
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
        @Part("telefonoContacto") telefonoContacto: RequestBody?,
        @Part("observaciones") observaciones: RequestBody?
    ): Response<ApiResponseWrapper<RecetaDigitalResponse>>
    
    /**
     * Cliente consulta sus recetas
     */
    @GET("api/recetas-digitales/cliente/{clienteId}")
    suspend fun getMisRecetas(
        @Path("clienteId") clienteId: Long
    ): Response<List<RecetaDigitalResponse>>
    
    /**
     * Obtener detalle de una receta
     */
    @GET("api/recetas-digitales/{id}")
    suspend fun getRecetaById(
        @Path("id") id: Long
    ): Response<RecetaDigitalResponse>
    
    // ========== ENDPOINTS PARA FARMACÉUTICO ==========
    
    /**
     * Farmacéutico obtiene recetas pendientes de procesar
     */
    @GET("api/recetas-digitales/pendientes")
    suspend fun getRecetasPendientes(): Response<ApiResponseWrapper<List<RecetaDigitalResponse>>>
    
    /**
     * Farmacéutico procesa receta y crea pedido con delivery
     */
    @POST("api/recetas-digitales/procesar-y-enviar")
    suspend fun procesarYEnviarReceta(
        @Body request: ProcesarRecetaRequest
    ): Response<ApiResponseWrapper<PedidoRecetaResponse>>
    
    /**
     * Farmacéutico rechaza una receta
     */
    @PUT("api/recetas-digitales/{id}/rechazar")
    suspend fun rechazarReceta(
        @Path("id") id: Long,
        @Query("motivo") motivo: String
    ): Response<ApiResponseWrapper<RecetaDigitalResponse>>
    
    /**
     * Farmacéutico procesa receta con OCR (venta presencial)
     */
    @Multipart
    @POST("api/recetas-digitales/procesar-farmaceutico")
    suspend fun procesarRecetaFarmaceutico(
        @Part imagen: MultipartBody.Part,
        @Part("observaciones") observaciones: RequestBody?
    ): Response<ApiResponseWrapper<RecetaDigitalResponse>>
}
