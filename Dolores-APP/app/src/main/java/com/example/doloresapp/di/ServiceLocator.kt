package com.example.doloresapp.di

import android.content.Context
import com.example.doloresapp.data.remote.service.ProductoApiService
import com.example.doloresapp.data.remote.api.*
import com.example.doloresapp.data.repository.OfflineProductoRepository
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
    private var offlineRepository: OfflineProductoRepository? = null
    
    @Volatile
    private var appContext: Context? = null
    
    @Volatile
    private var qrApiService: QRApiService? = null
    
    @Volatile
    private var recetaApiService: RecetaDigitalApiService? = null
    
    @Volatile
    private var fidelizacionApiService: FidelizacionApiService? = null
    
    @Volatile
    private var notificacionApiService: NotificacionApiService? = null
    
    /**
     * Inicializa el ServiceLocator con el contexto de la aplicación
     * Llamar desde Application.onCreate()
     */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun getApiService(): ProductoApiService {
        return apiService ?: synchronized(this) {
            apiService ?: NetworkClient.createService(ProductoApiService::class.java).also { apiService = it }
        }
    }

    fun getRepository(): ProductoRepository {
        // Si tenemos contexto, usar el repositorio offline
        appContext?.let { ctx ->
            return getOfflineRepository(ctx)
        }
        // Fallback al repositorio original si no hay contexto
        return repository ?: synchronized(this) {
            repository ?: ProductoRepositoryImpl(getApiService()).also { repository = it }
        }
    }
    
    fun getOfflineRepository(context: Context): OfflineProductoRepository {
        return offlineRepository ?: synchronized(this) {
            offlineRepository ?: OfflineProductoRepository(
                context.applicationContext,
                getApiService()
            ).also { offlineRepository = it }
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
