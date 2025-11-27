package com.example.doloresapp

import android.app.Application
import com.example.doloresapp.data.cart.CartRepository
import com.example.doloresapp.data.sync.SyncManager
import com.example.doloresapp.di.ServiceLocator

/**
 * Clase Application para inicializar componentes globales
 */
class DoloresApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar ServiceLocator con contexto
        ServiceLocator.init(this)
        
        // Inicializar CartRepository
        CartRepository.init(this)
        
        // Inicializar SyncManager para sincronización automática
        SyncManager.getInstance(this)
    }
}
