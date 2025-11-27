package com.example.doloresapp.data.sync

import android.content.Context
import android.util.Log
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.PedidoPendienteEntity
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.api.PedidoApiService
import com.example.doloresapp.data.remote.dto.CrearPedidoRequest
import com.example.doloresapp.data.remote.dto.PedidoDetalleRequest
import com.example.doloresapp.utils.NetworkUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Gestor de sincronización para pedidos offline
 */
class SyncManager private constructor(private val context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val pedidoPendienteDao = database.pedidoPendienteDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()
    
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState = _syncState.asStateFlow()
    
    private val _pendingCount = MutableStateFlow(0)
    val pendingCount = _pendingCount.asStateFlow()
    
    companion object {
        private const val TAG = "SyncManager"
        private const val MAX_RETRY_ATTEMPTS = 3
        
        @Volatile
        private var INSTANCE: SyncManager? = null
        
        fun getInstance(context: Context): SyncManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SyncManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    init {
        observeNetworkAndSync()
        updatePendingCount()
    }
    
    private fun observeNetworkAndSync() {
        scope.launch {
            NetworkUtils.observeNetworkState(context).collect { isConnected ->
                if (isConnected) {
                    Log.d(TAG, "Red disponible, iniciando sincronización...")
                    syncPendingOrders()
                }
            }
        }
    }
    
    private fun updatePendingCount() {
        scope.launch {
            pedidoPendienteDao.getCountPendientes().collect { count ->
                _pendingCount.value = count
            }
        }
    }
    
    /**
     * Guarda un pedido para sincronización posterior
     */
    suspend fun savePendingOrder(
        clienteId: Long,
        direccionId: Long?,
        direccionEntrega: String,
        telefono: String?,
        notas: String?,
        metodoPago: String,
        latitud: Double?,
        longitud: Double?,
        detalles: List<PedidoDetalleRequest>,
        total: Double
    ): Long {
        val detallesJson = gson.toJson(detalles)
        
        val pedido = PedidoPendienteEntity(
            clienteId = clienteId,
            direccionId = direccionId,
            direccionEntrega = direccionEntrega,
            telefono = telefono,
            notas = notas,
            metodoPago = metodoPago,
            latitud = latitud,
            longitud = longitud,
            detallesJson = detallesJson,
            total = total
        )
        
        val id = pedidoPendienteDao.insertPedido(pedido)
        Log.d(TAG, "Pedido guardado localmente con ID: $id")
        
        // Intentar sincronizar inmediatamente si hay conexión
        if (NetworkUtils.isNetworkAvailable(context)) {
            syncPendingOrders()
        }
        
        return id
    }
    
    /**
     * Sincroniza todos los pedidos pendientes
     */
    suspend fun syncPendingOrders(): SyncResult {
        if (_syncState.value == SyncState.Syncing) {
            return SyncResult(0, 0, "Sincronización en progreso")
        }
        
        _syncState.value = SyncState.Syncing
        
        val pendingOrders = pedidoPendienteDao.getPedidosPendientesList()
        if (pendingOrders.isEmpty()) {
            _syncState.value = SyncState.Idle
            return SyncResult(0, 0, "No hay pedidos pendientes")
        }
        
        Log.d(TAG, "Sincronizando ${pendingOrders.size} pedidos pendientes...")
        
        var successCount = 0
        var failCount = 0
        
        val api = try {
            NetworkClient.createService(PedidoApiService::class.java)
        } catch (e: Exception) {
            _syncState.value = SyncState.Error("Error de conexión")
            return SyncResult(0, pendingOrders.size, "Error de conexión: ${e.message}")
        }
        
        for (pedido in pendingOrders) {
            if (pedido.intentosSincronizacion >= MAX_RETRY_ATTEMPTS) {
                Log.w(TAG, "Pedido ${pedido.id} excedió máximo de intentos")
                continue
            }
            
            try {
                val detallesType = object : TypeToken<List<PedidoDetalleRequest>>() {}.type
                val detalles: List<PedidoDetalleRequest> = gson.fromJson(pedido.detallesJson, detallesType)
                
                val request = CrearPedidoRequest(
                    clienteId = pedido.clienteId,
                    direccionId = pedido.direccionId,
                    direccionEntrega = pedido.direccionEntrega,
                    telefono = pedido.telefono,
                    notas = pedido.notas,
                    metodoPago = pedido.metodoPago,
                    latitud = pedido.latitud,
                    longitud = pedido.longitud,
                    detalles = detalles
                )
                
                val response = api.crearPedido(request)
                Log.d(TAG, "Pedido ${pedido.id} sincronizado -> ID servidor: ${response.id}")
                
                pedidoPendienteDao.marcarComoSincronizado(pedido.id)
                successCount++
                
            } catch (e: Exception) {
                Log.e(TAG, "Error sincronizando pedido ${pedido.id}: ${e.message}")
                pedidoPendienteDao.incrementarIntentos(pedido.id, e.message)
                failCount++
            }
        }
        
        // Limpiar pedidos ya sincronizados
        pedidoPendienteDao.limpiarSincronizados()
        
        _syncState.value = if (failCount == 0) SyncState.Success else SyncState.PartialSuccess
        
        return SyncResult(successCount, failCount, "Sincronizados: $successCount, Fallidos: $failCount")
    }
    
    fun getPendingOrdersFlow(): Flow<List<PedidoPendienteEntity>> {
        return pedidoPendienteDao.getPedidosPendientes()
    }
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    object PartialSuccess : SyncState()
    data class Error(val message: String) : SyncState()
}

data class SyncResult(
    val successCount: Int,
    val failCount: Int,
    val message: String
)
