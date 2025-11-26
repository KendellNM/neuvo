package com.example.doloresapp.di

import com.example.doloresapp.data.remote.service.ProductoApiService
import com.example.doloresapp.data.remote.api.*
import com.example.doloresapp.data.repository.ProductoRepositoryImpl
import com.example.doloresapp.domain.repository.ProductoRepository
import com.example.doloresapp.domain.usecase.GetCategoriasUseCase
import com.example.doloresapp.domain.usecase.GetProductosUseCase
import com.example.doloresapp.data.remote.NetworkClient

object ServiceLocator {
    @Volatile
    private var apiService: ProductoApiService? = null

    @Volatile
    private var repository: ProductoRepository? = null
    
    @Volatile
    private var qrApiService: QRApiService? = null
    
    @Volatile
    private var recetaApiService: RecetaDigitalApiService? = null
    
    @Volatile
    private var fidelizacionApiService: FidelizacionApiService? = null
    
    @Volatile
    private var notificacionApiService: NotificacionApiService? = null

    fun getApiService(): ProductoApiService {
        return apiService ?: synchronized(this) {
            apiService ?: NetworkClient.createService(ProductoApiService::class.java).also { apiService = it }
        }
    }

    fun getRepository(): ProductoRepository {
        return repository ?: synchronized(this) {
            repository ?: ProductoRepositoryImpl(getApiService()).also { repository = it }
        }
    }

    fun getProductosUseCase(): GetProductosUseCase = GetProductosUseCase(getRepository())

    fun getCategoriasUseCase(): GetCategoriasUseCase = GetCategoriasUseCase(getRepository())
    
    fun provideQRApiService(): QRApiService {
        return qrApiService ?: synchronized(this) {
            qrApiService ?: NetworkClient.createService(QRApiService::class.java).also { qrApiService = it }
        }
    }
    
    fun provideRecetaApiService(): RecetaDigitalApiService {
        return recetaApiService ?: synchronized(this) {
            recetaApiService ?: NetworkClient.createService(RecetaDigitalApiService::class.java).also { recetaApiService = it }
        }
    }
    
    fun provideFidelizacionApiService(): FidelizacionApiService {
        return fidelizacionApiService ?: synchronized(this) {
            fidelizacionApiService ?: NetworkClient.createService(FidelizacionApiService::class.java).also { fidelizacionApiService = it }
        }
    }
    
    fun provideNotificacionApiService(): NotificacionApiService {
        return notificacionApiService ?: synchronized(this) {
            notificacionApiService ?: NetworkClient.createService(NotificacionApiService::class.java).also { notificacionApiService = it }
        }
    }
}
