package com.example.doloresapp.data.local.dao

import androidx.room.*
import com.example.doloresapp.data.local.entity.PedidoPendienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoPendienteDao {
    
    @Query("SELECT * FROM pedidos_pendientes WHERE sincronizado = 0 ORDER BY fechaCreacion ASC")
    fun getPedidosPendientes(): Flow<List<PedidoPendienteEntity>>
    
    @Query("SELECT * FROM pedidos_pendientes WHERE sincronizado = 0 ORDER BY fechaCreacion ASC")
    suspend fun getPedidosPendientesList(): List<PedidoPendienteEntity>
    
    @Query("SELECT COUNT(*) FROM pedidos_pendientes WHERE sincronizado = 0")
    fun getCountPendientes(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM pedidos_pendientes WHERE sincronizado = 0")
    suspend fun getCountPendientesSync(): Int
    
    @Insert
    suspend fun insertPedido(pedido: PedidoPendienteEntity): Long
    
    @Update
    suspend fun updatePedido(pedido: PedidoPendienteEntity)
    
    @Query("UPDATE pedidos_pendientes SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarComoSincronizado(id: Long)
    
    @Query("UPDATE pedidos_pendientes SET intentosSincronizacion = intentosSincronizacion + 1, ultimoError = :error WHERE id = :id")
    suspend fun incrementarIntentos(id: Long, error: String?)
    
    @Delete
    suspend fun deletePedido(pedido: PedidoPendienteEntity)
    
    @Query("DELETE FROM pedidos_pendientes WHERE sincronizado = 1")
    suspend fun limpiarSincronizados()
    
    @Query("SELECT * FROM pedidos_pendientes ORDER BY fechaCreacion DESC")
    fun getAllPedidos(): Flow<List<PedidoPendienteEntity>>
}
